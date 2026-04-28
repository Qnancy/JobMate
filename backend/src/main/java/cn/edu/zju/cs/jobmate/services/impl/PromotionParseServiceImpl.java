package cn.edu.zju.cs.jobmate.services.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.edu.zju.cs.jobmate.configs.JsonConfig;
import cn.edu.zju.cs.jobmate.configs.properties.LlmProperties;
import cn.edu.zju.cs.jobmate.dto.admin.ParsedActivityPromotionDto;
import cn.edu.zju.cs.jobmate.dto.admin.ParsedJobPromotionDto;
import cn.edu.zju.cs.jobmate.dto.admin.PromotionParseRequest;
import cn.edu.zju.cs.jobmate.dto.admin.PromotionParseResponse;
import cn.edu.zju.cs.jobmate.dto.admin.PromotionParseTarget;
import cn.edu.zju.cs.jobmate.enums.ActivityType;
import cn.edu.zju.cs.jobmate.enums.EducationRequirement;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.services.PromotionParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OpenAI-compatible chat completions client for promotion parsing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionParseServiceImpl implements PromotionParseService {

    private static final DateTimeFormatter[] ACTIVITY_TIME_PARSERS = new DateTimeFormatter[] {
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        JsonConfig.DATETIME_FORMAT,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
    };

    private final LlmProperties llmProperties;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build();

    @Override
    public PromotionParseResponse parse(PromotionParseRequest request) {
        if (!llmProperties.isEnabled() || !llmProperties.hasApiKey()) {
            throw new BusinessException(ErrorCode.LLM_NOT_CONFIGURED);
        }
        String systemPrompt = request.getTarget() == PromotionParseTarget.JOB
            ? jobSystemPrompt()
            : activitySystemPrompt();
        String content = callChatCompletions(systemPrompt, request.getText());
        JsonNode root = parseModelJson(content);
        List<String> warnings = new ArrayList<>();
        if (request.getTarget() == PromotionParseTarget.JOB) {
            ParsedJobPromotionDto job = buildJobDto(root, warnings);
            return PromotionParseResponse.builder()
                .warnings(warnings)
                .suggestedCompanyName(computeSuggestedCompanyName(job.getCompanyId(), job.getCompanyName()))
                .job(job)
                .build();
        }
        ParsedActivityPromotionDto activity = buildActivityDto(root, warnings);
        return PromotionParseResponse.builder()
            .warnings(warnings)
            .suggestedCompanyName(computeSuggestedCompanyName(activity.getCompanyId(), activity.getCompanyName()))
            .activity(activity)
            .build();
    }

    /**
     * If the parsed company name could not be resolved to an existing DB row,
     * return that raw name so the admin UI can offer a "create company" shortcut.
     * Otherwise, return null (UI will hide the shortcut).
     */
    private String computeSuggestedCompanyName(Long resolvedCompanyId, String companyName) {
        if (resolvedCompanyId != null && resolvedCompanyId > 0) {
            return null;
        }
        if (companyName == null || companyName.isBlank()) {
            return null;
        }
        return companyName.trim();
    }

    private String jobSystemPrompt() {
        return """
            You extract structured job posting data from unstructured Chinese or English text.
            Reply with a single JSON object only (no markdown), using exactly these keys:
            company_name (string, full company name if known, else empty string),
            company_id (number or null, only if an explicit numeric id appears in the text),
            position (string, job title),
            recruit_type (string, one of INTERN, CAMPUS — map 实习 to INTERN, anything else (校招/应届/社招/experienced) to CAMPUS; default INTERN if unclear),
            link (string or null, application or detail URL),
            location (string or null, city or office; if multiple bases, join with Chinese enumeration comma 、 e.g. 北京、上海、杭州),
            deadline (string or null, application deadline if mentioned; prefer format yyyy-MM-dd HH:mm:ss; if only date, use 23:59:59 as default time; null when not stated),
            education_requirement (string, one of UNSPECIFIED, BACHELOR_AND_ABOVE, MASTER_AND_ABOVE, PHD_AND_ABOVE;
                map 本科/bachelor/学士 to BACHELOR_AND_ABOVE, 硕士/master/研究生 to MASTER_AND_ABOVE, 博士/phd to PHD_AND_ABOVE;
                use UNSPECIFIED when not stated or unclear),
            extra (string or null, other important info not fitting other fields).
            Use null for unknown optional fields. Do not invent URLs or deadlines.
            """;
    }

    private String activitySystemPrompt() {
        return """
            You extract campus talk / career fair / company-visit event data from unstructured Chinese or English text.
            Reply with a single JSON object only (no markdown), using exactly these keys:
            company_name (string),
            company_id (number or null, only if explicit in text),
            title (string, event title),
            time (string, local date-time in China; prefer format yyyy-MM-dd HH:mm:ss; if only date, use 14:00:00 as default time),
            type (string, one of LECTURE, JOB_FAIR, COMPANY_VISIT — map 宣讲会/分享会/info session to LECTURE,
                  双选会/招聘会/career fair to JOB_FAIR, 名企探访/参观/开放日/open day/企业行 to COMPANY_VISIT;
                  default LECTURE if unclear),
            link (string or null),
            location (string or null, venue or online meeting info),
            extra (string or null).
            Use null for unknown optional fields. Do not invent URLs.
            """;
    }

    private String callChatCompletions(String systemPrompt, String userText) {
        String url = trimTrailingSlash(llmProperties.getBaseUrl()) + "/chat/completions";
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", llmProperties.getModel());
            body.put("temperature", 0.2);
            ObjectNode rf = body.putObject("response_format");
            rf.put("type", "json_object");
            ArrayNode messages = body.putArray("messages");
            ObjectNode sys = messages.addObject();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            ObjectNode user = messages.addObject();
            user.put("role", "user");
            user.put("content", userText);

            String json = objectMapper.writeValueAsString(body);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Authorization", "Bearer " + llmProperties.getApiKey().trim())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("LLM HTTP {} body snippet: {}", response.statusCode(),
                    response.body() != null && response.body().length() > 500
                        ? response.body().substring(0, 500) : response.body());
                throw new BusinessException(ErrorCode.LLM_UPSTREAM_ERROR);
            }
            JsonNode root = objectMapper.readTree(response.body());
            if (root.hasNonNull("error")) {
                log.warn("LLM error payload: {}", root.get("error"));
                throw new BusinessException(ErrorCode.LLM_UPSTREAM_ERROR);
            }
            return root.path("choices").path(0).path("message").path("content").asText("");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("LLM request failed", e);
            throw new BusinessException(ErrorCode.LLM_UPSTREAM_ERROR, e);
        }
    }

    private JsonNode parseModelJson(String rawContent) {
        String text = stripMarkdownFence(rawContent);
        if (text.isBlank()) {
            throw new BusinessException(ErrorCode.LLM_INVALID_OUTPUT);
        }
        try {
            return objectMapper.readTree(text);
        } catch (Exception e) {
            log.warn("Failed to parse model JSON: {}", text.length() > 200 ? text.substring(0, 200) : text);
            throw new BusinessException(ErrorCode.LLM_INVALID_OUTPUT, e);
        }
    }

    private ParsedJobPromotionDto buildJobDto(JsonNode n, List<String> warnings) {
        String companyName = textOrEmpty(n, "company_name");
        Long llmCompanyId = readOptionalLong(n, "company_id");
        Long resolved = resolveCompanyId(llmCompanyId, companyName, warnings);

        String position = textOrEmpty(n, "position");
        if (position.isBlank()) {
            warnings.add("未能识别职位名称，请手动填写。");
        }

        String recruit = normalizeRecruitType(textOrEmpty(n, "recruit_type"), warnings);

        // Reuse activity time normaliser; for jobs we use 23:59:59 as the assumed
        // closing time when only a date is given.
        String deadline = normalizeJobDeadline(textOrEmpty(n, "deadline"));

        String educationRequirement = normalizeEducationRequirement(textOrEmpty(n, "education_requirement"), warnings);

        return ParsedJobPromotionDto.builder()
            .companyId(resolved != null ? resolved : 0L)
            .companyName(companyName.isBlank() ? null : companyName)
            .position(position)
            .recruitType(recruit)
            .link(nullIfBlank(textOrNull(n, "link")))
            .location(nullIfBlank(textOrNull(n, "location")))
            .extra(nullIfBlank(textOrNull(n, "extra")))
            .deadline(deadline.isBlank() ? null : deadline)
            .educationRequirement(educationRequirement.isBlank() ? null : educationRequirement)
            .build();
    }

    /**
     * Map LLM output (English enum name or Chinese JD wording) to {@link EducationRequirement} name.
     * Returns empty string when {@link EducationRequirement#UNSPECIFIED}.
     */
    private String normalizeEducationRequirement(String raw, List<String> warnings) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String s = raw.trim();
        String u = s.toUpperCase().replace('-', '_').replace(' ', '_');
        if ("UNSPECIFIED".equals(u)) {
            return "";
        }
        try {
            EducationRequirement er = EducationRequirement.valueOf(u);
            return er == EducationRequirement.UNSPECIFIED ? "" : er.name();
        } catch (IllegalArgumentException ignored) {
            // fall through to keyword mapping
        }
        if (u.contains("PHD") || s.contains("博士")) {
            return EducationRequirement.PHD_AND_ABOVE.name();
        }
        if (u.contains("MASTER") || s.contains("硕士")) {
            return EducationRequirement.MASTER_AND_ABOVE.name();
        }
        if (u.contains("BACHELOR") || s.contains("本科") || s.contains("学士")) {
            return EducationRequirement.BACHELOR_AND_ABOVE.name();
        }
        if (s.contains("学历不限") || s.contains("不限学历")) {
            return "";
        }
        warnings.add("未能可靠识别学历要求，请在表单中手动选择。");
        return "";
    }

    /**
     * Same parsers as {@link #normalizeActivityTime}, but for date-only deadlines we
     * default to 23:59:59 (the entire day is still open).
     */
    private String normalizeJobDeadline(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String s = raw.trim();
        for (DateTimeFormatter f : ACTIVITY_TIME_PARSERS) {
            try {
                LocalDateTime dt = LocalDateTime.parse(s, f);
                return dt.format(JsonConfig.DATETIME_FORMAT);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }
        if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                return LocalDate.parse(s).atTime(23, 59, 59).format(JsonConfig.DATETIME_FORMAT);
            } catch (DateTimeParseException e) {
                return "";
            }
        }
        return "";
    }

    private ParsedActivityPromotionDto buildActivityDto(JsonNode n, List<String> warnings) {
        String companyName = textOrEmpty(n, "company_name");
        Long llmCompanyId = readOptionalLong(n, "company_id");
        Long resolved = resolveCompanyId(llmCompanyId, companyName, warnings);

        String title = textOrEmpty(n, "title");
        if (title.isBlank()) {
            warnings.add("未能识别活动标题，请手动填写。");
        }

        String timeRaw = textOrEmpty(n, "time");
        String timeNorm = normalizeActivityTime(timeRaw);
        if (timeNorm.isBlank()) {
            warnings.add("未能识别活动时间，请手动填写（格式建议 yyyy-MM-dd HH:mm）。");
        }

        String type = normalizeActivityType(textOrEmpty(n, "type"), title, warnings);

        return ParsedActivityPromotionDto.builder()
            .companyId(resolved != null ? resolved : 0L)
            .companyName(companyName.isBlank() ? null : companyName)
            .title(title)
            .time(timeNorm)
            .type(type)
            .link(nullIfBlank(textOrNull(n, "link")))
            .location(nullIfBlank(textOrNull(n, "location")))
            .extra(nullIfBlank(textOrNull(n, "extra")))
            .build();
    }

    /**
     * Map the LLM's free-form `type` field (or, as a fallback, the title) onto a valid
     * {@link ActivityType} name. Unknown values fall back to LECTURE with a warning.
     */
    private String normalizeActivityType(String raw, String title, List<String> warnings) {
        String s = raw == null ? "" : raw.trim();
        String u = s.toUpperCase();
        if ("LECTURE".equals(u) || s.contains("宣讲") || s.contains("分享")) {
            return ActivityType.LECTURE.name();
        }
        if ("JOB_FAIR".equals(u) || "JOBFAIR".equals(u) || s.contains("双选") || s.contains("招聘会")) {
            return ActivityType.JOB_FAIR.name();
        }
        if ("COMPANY_VISIT".equals(u) || "COMPANYVISIT".equals(u)
            || s.contains("探访") || s.contains("参观") || s.contains("开放日") || s.contains("名企行")) {
            return ActivityType.COMPANY_VISIT.name();
        }

        // Fallback: try to infer from the title before giving up.
        String t = title == null ? "" : title;
        if (t.contains("双选") || t.contains("招聘会")) return ActivityType.JOB_FAIR.name();
        if (t.contains("探访") || t.contains("参观") || t.contains("开放日") || t.contains("名企行")) {
            return ActivityType.COMPANY_VISIT.name();
        }
        if (t.contains("宣讲") || t.contains("分享")) return ActivityType.LECTURE.name();

        if (!s.isBlank()) {
            warnings.add("未能识别活动类型，已默认为宣讲会(LECTURE)，请按需调整。");
        }
        return ActivityType.LECTURE.name();
    }

    /**
     * Normalises the LLM's free-form `recruit_type` into the two product-facing options.
     * "Experienced / 社招" is intentionally folded into CAMPUS — the social-recruiting
     * tag has been retired from the product surface, but the enum still exists in the
     * DB to preserve historical rows.
     */
    private String normalizeRecruitType(String raw, List<String> warnings) {
        if (raw == null || raw.isBlank()) {
            warnings.add("招聘类型未识别，已默认设为实习(INTERN)。");
            return RecruitType.INTERN.name();
        }
        String s = raw.trim();
        String u = s.toUpperCase();
        if ("INTERN".equals(u) || s.contains("实习")) {
            return RecruitType.INTERN.name();
        }
        if ("CAMPUS".equals(u)
            || "EXPERIENCED".equals(u)
            || s.contains("校招") || s.contains("应届") || s.contains("社招")) {
            return RecruitType.CAMPUS.name();
        }
        warnings.add("招聘类型未识别或无效，已默认设为实习(INTERN)。");
        return RecruitType.INTERN.name();
    }

    private Long resolveCompanyId(Long llmId, String companyName, List<String> warnings) {
        if (llmId != null && llmId > 0 && companyRepository.existsById(llmId)) {
            return llmId;
        }
        if (llmId != null && llmId > 0) {
            warnings.add("文本中的公司 ID 在数据库中不存在，已忽略该 ID。");
        }
        if (companyName != null && !companyName.isBlank()) {
            Optional<Company> c = companyRepository.findByName(companyName.trim());
            if (c.isPresent()) {
                return c.get().getId();
            }
            warnings.add("数据库中未找到与「" + companyName.trim() + "」完全一致的公司名称，请先在企业管理中添加该公司，或手动填写公司 ID。");
        } else if (llmId == null || llmId <= 0) {
            warnings.add("未能从文本中识别公司名称，请手动填写公司 ID。");
        }
        return null;
    }

    private static String textOrEmpty(JsonNode n, String field) {
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) {
            return "";
        }
        return v.asText("").trim();
    }

    private static String textOrNull(JsonNode n, String field) {
        JsonNode v = n.get(field);
        if (v == null || v.isNull()) {
            return null;
        }
        String s = v.asText("").trim();
        return s.isEmpty() ? null : s;
    }

    private static Long readOptionalLong(JsonNode n, String field) {
        JsonNode v = n.get(field);
        if (v == null || v.isNull() || v.isMissingNode()) {
            return null;
        }
        if (v.isIntegralNumber()) {
            long x = v.longValue();
            return x > 0 ? x : null;
        }
        String s = v.asText("").trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            long x = Long.parseLong(s);
            return x > 0 ? x : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String nullIfBlank(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s;
    }

    private String normalizeActivityTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String s = raw.trim();
        for (DateTimeFormatter f : ACTIVITY_TIME_PARSERS) {
            try {
                LocalDateTime dt = LocalDateTime.parse(s, f);
                return dt.format(JsonConfig.DATETIME_FORMAT);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }
        if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                return LocalDate.parse(s).atTime(14, 0).format(JsonConfig.DATETIME_FORMAT);
            } catch (DateTimeParseException e) {
                return "";
            }
        }
        return "";
    }

    private static String stripMarkdownFence(String s) {
        if (s == null) {
            return "";
        }
        String t = s.trim();
        if (!t.startsWith("```")) {
            return t;
        }
        int lastFence = t.lastIndexOf("```");
        String inner = lastFence > 3 ? t.substring(3, lastFence).trim() : t.substring(3).trim();
        if (inner.startsWith("json")) {
            inner = inner.substring(4).trim();
        }
        return inner;
    }

    private static String trimTrailingSlash(String base) {
        if (base == null || base.isEmpty()) {
            return "https://api.openai.com/v1";
        }
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }
}
