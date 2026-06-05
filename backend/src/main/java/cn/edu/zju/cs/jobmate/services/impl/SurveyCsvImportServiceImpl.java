package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.dto.admin.ImportConflictAction;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportConfirmRequest;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportExistingCompany;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportExistingJob;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportPreviewResponse;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportPreviewRow;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportResponse;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportRowPayload;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportRowResult;
import cn.edu.zju.cs.jobmate.dto.company.CompanyCreateRequest;
import cn.edu.zju.cs.jobmate.dto.company.CompanyUpdateRequest;
import cn.edu.zju.cs.jobmate.dto.job.JobInfoCreateRequest;
import cn.edu.zju.cs.jobmate.dto.job.JobInfoUpdateRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.EducationRequirement;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import cn.edu.zju.cs.jobmate.services.SurveyCsvImportService;
import cn.edu.zju.cs.jobmate.utils.csv.CsvTextParser;
import cn.edu.zju.cs.jobmate.utils.csv.JinshujuCsvHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Parses Jinshuju employer survey CSV, previews rows, then imports after admin confirmation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyCsvImportServiceImpl implements SurveyCsvImportService {

    private static final DateTimeFormatter[] DEADLINE_DATE_TIME_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy/M/d H:mm"),
        DateTimeFormatter.ofPattern("yyyy/M/d HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
    };

    private static final DateTimeFormatter[] DEADLINE_DATE_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy/M/d"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    };

    private final CompanyService companyService;
    private final JobInfoService jobInfoService;
    private final CompanyRepository companyRepository;
    private final JobInfoRepository jobInfoRepository;

    @Override
    @Transactional(readOnly = true)
    public SurveyImportPreviewResponse previewCsv(MultipartFile file) {
        ParsedCsv parsed = readCsv(file);
        List<SurveyImportPreviewRow> rows = new ArrayList<>();
        int valid = 0;
        int invalid = 0;

        for (int i = 1; i < parsed.rows().size(); i++) {
            String[] values = parsed.rows().get(i);
            if (isEmptyRow(values)) {
                continue;
            }
            Map<String, String> row = JinshujuCsvHeaders.rowToMap(parsed.header(), values);
            int rowNumber = i + 1;
            SurveyImportPreviewRow preview = buildPreviewRow(rowNumber, row);
            rows.add(preview);
            if (preview.isValid()) {
                valid++;
            } else {
                invalid++;
            }
        }

        return SurveyImportPreviewResponse.builder()
            .fileName(parsed.fileName())
            .totalRows(rows.size())
            .validCount(valid)
            .invalidCount(invalid)
            .rows(rows)
            .build();
    }

    @Override
    @Transactional
    public SurveyImportResponse importRows(SurveyImportConfirmRequest request) {
        List<SurveyImportRowResult> results = new ArrayList<>();
        int success = 0;
        int failed = 0;

        for (SurveyImportRowPayload payload : request.getRows()) {
            SurveyImportRowResult rowResult = importPayload(payload);
            results.add(rowResult);
            if (rowResult.isSuccess()) {
                success++;
            } else {
                failed++;
            }
        }

        return SurveyImportResponse.builder()
            .fileName(request.getFileName())
            .totalRows(results.size())
            .successCount(success)
            .failedCount(failed)
            .rows(results)
            .build();
    }

    private SurveyImportPreviewRow buildPreviewRow(int rowNumber, Map<String, String> row) {
        String companyName = JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_FULL_NAME);
        String rowLabel = resolveRowLabel(rowNumber, row);
        String infoType = JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.INFO_TYPE);
        String companyTypeRaw = JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_TYPE);

        if (JinshujuCsvHeaders.isBlank(companyName)) {
            return invalidPreview(rowNumber, rowLabel, infoType, companyName, companyTypeRaw,
                "企业全称不能为空", null);
        }

        try {
            if (JinshujuCsvHeaders.INFO_TYPE_COMPANY.equals(infoType)) {
                return previewCompanyPromotion(rowNumber, rowLabel, row, companyName, companyTypeRaw, infoType);
            }
            if (JinshujuCsvHeaders.INFO_TYPE_JOB.equals(infoType)) {
                return previewJobPromotion(rowNumber, rowLabel, row, companyName, companyTypeRaw, infoType);
            }
            return invalidPreview(rowNumber, rowLabel, infoType, companyName, companyTypeRaw,
                "不支持的招聘信息类型：" + infoType, null);
        } catch (BusinessException e) {
            return invalidPreview(rowNumber, rowLabel, infoType, companyName, companyTypeRaw,
                e.getMessage(), buildPayload(rowNumber, rowLabel, infoType, row));
        }
    }

    private SurveyImportPreviewRow previewCompanyPromotion(
        int rowNumber,
        String rowLabel,
        Map<String, String> row,
        String companyName,
        String companyTypeRaw,
        String infoType
    ) {
        CompanyType type = parseCompanyType(companyTypeRaw);
        SurveyImportRowPayload payload = buildPayload(rowNumber, rowLabel, infoType, row);

        Optional<Company> existing = companyRepository.findByName(companyName);
        if (existing.isPresent()) {
            return SurveyImportPreviewRow.builder()
                .rowNumber(rowNumber)
                .rowLabel(rowLabel)
                .valid(true)
                .infoType(infoType)
                .companyName(companyName)
                .companyType(companyTypeRaw)
                .hasConflict(true)
                .existingCompany(SurveyImportExistingCompany.from(existing.get()))
                .plannedAction("COMPANY_WILL_UPDATE")
                .summary("网站已有企业「" + companyName + "」，请选择覆盖或保留")
                .payload(payload)
                .build();
        }

        return SurveyImportPreviewRow.builder()
            .rowNumber(rowNumber)
            .rowLabel(rowLabel)
            .valid(true)
            .infoType(infoType)
            .companyName(companyName)
            .companyType(companyTypeRaw)
            .hasConflict(false)
            .plannedAction("COMPANY_WILL_CREATE")
            .summary("新建企业「" + companyName + "」"
                + (type != null ? "（" + companyTypeRaw + "）" : ""))
            .payload(payload)
            .build();
    }

    private SurveyImportPreviewRow previewJobPromotion(
        int rowNumber,
        String rowLabel,
        Map<String, String> row,
        String companyName,
        String companyTypeRaw,
        String infoType
    ) {
        String position = JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.POSITION);
        if (JinshujuCsvHeaders.isBlank(position)) {
            return invalidPreview(rowNumber, rowLabel, infoType, companyName, companyTypeRaw,
                "定向岗位宣传需填写职位名称", buildPayload(rowNumber, rowLabel, infoType, row));
        }

        String recruitRaw = JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.RECRUIT_TYPE);
        if (JinshujuCsvHeaders.isBlank(recruitRaw)) {
            return invalidPreview(rowNumber, rowLabel, infoType, companyName, companyTypeRaw,
                "定向岗位宣传需填写招聘类型", buildPayload(rowNumber, rowLabel, infoType, row));
        }

        parseCompanyType(companyTypeRaw);
        RecruitType recruitType = parseRecruitType(recruitRaw);
        SurveyImportRowPayload payload = buildPayload(rowNumber, rowLabel, infoType, row);

        Optional<Company> companyOpt = companyRepository.findByName(companyName);
        String companyPart = companyOpt.isPresent() ? "" : "，并新建企业";

        if (companyOpt.isPresent()) {
            Optional<JobInfo> existingJob = jobInfoRepository.findFirstByCompanyIdAndPositionAndRecruitType(
                companyOpt.get().getId(),
                position,
                recruitType
            );
            if (existingJob.isPresent()) {
                return SurveyImportPreviewRow.builder()
                    .rowNumber(rowNumber)
                    .rowLabel(rowLabel)
                    .valid(true)
                    .infoType(infoType)
                    .companyName(companyName)
                    .companyType(companyTypeRaw)
                    .hasConflict(true)
                    .existingCompany(SurveyImportExistingCompany.from(companyOpt.get()))
                    .existingJob(SurveyImportExistingJob.from(existingJob.get()))
                    .plannedAction("JOB_WILL_UPDATE")
                    .summary("网站已有职位「" + position + "」，请选择覆盖或保留")
                    .payload(payload)
                    .build();
            }
        }

        return SurveyImportPreviewRow.builder()
            .rowNumber(rowNumber)
            .rowLabel(rowLabel)
            .valid(true)
            .infoType(infoType)
            .companyName(companyName)
            .companyType(companyTypeRaw)
            .hasConflict(false)
            .plannedAction("JOB_WILL_CREATE")
            .summary("新建「" + companyName + "」职位「" + position + "」（" + recruitRaw + "）" + companyPart)
            .payload(payload)
            .build();
    }

    private SurveyImportRowResult importPayload(SurveyImportRowPayload payload) {
        int rowNumber = payload.getRowNumber() != null ? payload.getRowNumber() : 0;
        String rowLabel = resolveRowLabel(rowNumber, payload.getRowLabel());
        String companyName = payload.getCompanyName();
        String infoType = payload.getInfoType();

        try {
            if (JinshujuCsvHeaders.INFO_TYPE_COMPANY.equals(infoType)) {
                return importCompanyFromPayload(rowNumber, rowLabel, payload);
            }
            if (JinshujuCsvHeaders.INFO_TYPE_JOB.equals(infoType)) {
                return importJobFromPayload(rowNumber, rowLabel, payload);
            }
            return fail(rowNumber, rowLabel, "不支持的招聘信息类型：" + infoType);
        } catch (BusinessException e) {
            return fail(rowNumber, rowLabel, e.getMessage());
        } catch (Exception e) {
            log.warn("Survey import row {} failed", rowNumber, e);
            return fail(rowNumber, rowLabel, "导入失败：" + e.getMessage());
        }
    }

    private SurveyImportRowResult importCompanyFromPayload(
        int rowNumber,
        String rowLabel,
        SurveyImportRowPayload payload
    ) {
        Optional<Company> existingOpt = companyRepository.findByName(payload.getCompanyName());
        if (existingOpt.isPresent()) {
            SurveyImportRowResult conflictResult = resolveConflictBeforeWrite(
                rowNumber,
                rowLabel,
                payload.getConflictAction(),
                existingOpt.get().getId(),
                null
            );
            if (conflictResult != null) {
                return conflictResult;
            }
        }

        CompanyType type = parseCompanyType(payload.getCompanyType());
        CompanyUpsertResult upsert = upsertCompany(
            payload.getCompanyName(),
            type,
            resolveCompanyDescription(payload)
        );
        return SurveyImportRowResult.builder()
            .rowNumber(rowNumber)
            .rowLabel(rowLabel)
            .success(true)
            .action(upsert.created() ? "COMPANY_CREATED" : "COMPANY_UPDATED")
            .message(upsert.created() ? "已创建企业并写入简介" : "已用新内容覆盖企业信息")
            .companyId(upsert.company().getId())
            .build();
    }

    private SurveyImportRowResult importJobFromPayload(
        int rowNumber,
        String rowLabel,
        SurveyImportRowPayload payload
    ) {
        String position = payload.getPosition();
        if (JinshujuCsvHeaders.isBlank(position)) {
            return fail(rowNumber, rowLabel, "定向岗位宣传需填写职位名称");
        }

        String recruitRaw = payload.getRecruitType();
        if (JinshujuCsvHeaders.isBlank(recruitRaw)) {
            return fail(rowNumber, rowLabel, "定向岗位宣传需填写招聘类型");
        }

        RecruitType recruitType = parseRecruitType(recruitRaw);
        CompanyType type = parseCompanyType(payload.getCompanyType());
        CompanyUpsertResult upsert = upsertCompany(payload.getCompanyName(), type, null);

        String link = normalizeLink(payload.getLink());
        String location = blankToNull(payload.getLocation());
        String extra = blankToNull(payload.getJobExtra());
        EducationRequirement education = parseEducation(payload.getEducation());
        LocalDateTime deadline = parseDeadline(payload.getDeadline());

        Optional<JobInfo> existingJob = jobInfoRepository.findFirstByCompanyIdAndPositionAndRecruitType(
            upsert.company().getId(),
            position,
            recruitType
        );

        if (existingJob.isPresent()) {
            SurveyImportRowResult conflictResult = resolveConflictBeforeWrite(
                rowNumber,
                rowLabel,
                payload.getConflictAction(),
                upsert.company().getId(),
                existingJob.get().getId()
            );
            if (conflictResult != null) {
                return conflictResult;
            }

            JobInfo job = existingJob.get();
            jobInfoService.update(job.getId(), JobInfoUpdateRequest.builder()
                .link(link)
                .location(location)
                .extra(extra)
                .educationRequirement(education)
                .deadline(deadline)
                .build());
            return SurveyImportRowResult.builder()
                .rowNumber(rowNumber)
                .rowLabel(rowLabel)
                .success(true)
                .action("JOB_UPDATED")
                .message("已用新内容覆盖职位：" + position)
                .companyId(upsert.company().getId())
                .jobId(job.getId())
                .build();
        }

        JobInfo created = jobInfoService.create(JobInfoCreateRequest.builder()
            .companyId(upsert.company().getId())
            .recruitType(recruitType)
            .position(position)
            .link(link)
            .location(location)
            .extra(extra)
            .educationRequirement(education)
            .deadline(deadline)
            .build());

        String companyAction = upsert.created() ? "并新建企业" : "";
        return SurveyImportRowResult.builder()
            .rowNumber(rowNumber)
            .rowLabel(rowLabel)
            .success(true)
            .action("JOB_CREATED")
            .message("已创建职位：" + position + companyAction)
            .companyId(upsert.company().getId())
            .jobId(created.getId())
            .build();
    }

    /**
     * @return non-null result when import should stop for this row; null means proceed with UPDATE
     */
    private SurveyImportRowResult resolveConflictBeforeWrite(
        int rowNumber,
        String rowLabel,
        String conflictAction,
        Long companyId,
        Long jobId
    ) {
        if (!ImportConflictAction.isResolved(conflictAction)) {
            return fail(rowNumber, rowLabel, "与网站已有数据冲突，请选择处理方式");
        }
        if (ImportConflictAction.SKIP.equals(conflictAction)) {
            return SurveyImportRowResult.builder()
                .rowNumber(rowNumber)
                .rowLabel(rowLabel)
                .success(true)
                .action("SKIPPED")
                .message("已跳过，保留网站现有数据")
                .companyId(companyId)
                .jobId(jobId)
                .build();
        }
        if (ImportConflictAction.KEEP_EXISTING.equals(conflictAction)) {
            return SurveyImportRowResult.builder()
                .rowNumber(rowNumber)
                .rowLabel(rowLabel)
                .success(true)
                .action("KEPT_EXISTING")
                .message("已保留网站现有数据，未作修改")
                .companyId(companyId)
                .jobId(jobId)
                .build();
        }
        return null;
    }

    private SurveyImportRowPayload buildPayload(
        int rowNumber,
        String rowLabel,
        String infoType,
        Map<String, String> row
    ) {
        String tagline = blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_TAGLINE));
        String intro = blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_DESCRIPTION));
        return SurveyImportRowPayload.builder()
            .rowNumber(rowNumber)
            .rowLabel(rowLabel)
            .infoType(infoType)
            .companyName(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_FULL_NAME))
            .companyType(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_TYPE)))
            .companyShortName(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_SHORT_NAME)))
            .companyWebsite(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_WEBSITE)))
            .companyTagline(tagline)
            .companyIntro(intro)
            .companyDescription(buildCompanyDescription(row))
            .position(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.POSITION)))
            .recruitType(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.RECRUIT_TYPE)))
            .education(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.EDUCATION)))
            .location(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.LOCATION)))
            .link(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.APPLY_LINK)))
            .deadline(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.DEADLINE)))
            .jobExtra(blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.JOB_DESCRIPTION)))
            .build();
    }

    private ParsedCsv readCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER, "请上传 CSV 文件");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".csv")) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "仅支持 .csv 文件");
        }

        String text;
        try {
            text = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "无法读取 CSV 文件");
        }

        List<String[]> parsed;
        try {
            parsed = CsvTextParser.parse(text);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, e.getMessage());
        }

        if (parsed.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "CSV 文件为空");
        }

        String[] header = parsed.get(0);
        validateHeader(header);
        return new ParsedCsv(originalName, header, parsed);
    }

    private void validateHeader(String[] header) {
        boolean hasCompanyName = false;
        boolean hasInfoType = false;
        for (String col : header) {
            if (JinshujuCsvHeaders.COMPANY_FULL_NAME.equals(col.trim())) {
                hasCompanyName = true;
            }
            if (JinshujuCsvHeaders.INFO_TYPE.equals(col.trim())) {
                hasInfoType = true;
            }
        }
        if (!hasCompanyName || !hasInfoType) {
            throw new BusinessException(
                ErrorCode.INVALID_PARAMETER,
                "CSV 表头不符合金数据用人单位问卷导出格式（缺少「企业全称」或「招聘信息类型」列）"
            );
        }
    }

    private CompanyUpsertResult upsertCompany(String name, CompanyType type, String description) {
        Optional<Company> existing = companyRepository.findByName(name);
        if (existing.isPresent()) {
            Company company = existing.get();
            var builder = CompanyUpdateRequest.builder();
            if (type != null) {
                builder.type(type);
            }
            if (description != null) {
                builder.description(description);
            }
            CompanyUpdateRequest update = builder.build();
            if (update.isUpdatable()) {
                company = companyService.update(company.getId(), update);
            }
            return new CompanyUpsertResult(company, false);
        }

        Company created = companyService.create(CompanyCreateRequest.builder()
            .name(name)
            .type(type != null ? type : CompanyType.PRIVATE)
            .description(description)
            .build());
        return new CompanyUpsertResult(created, true);
    }

    private static String buildCompanyDescription(Map<String, String> row) {
        return mergeCompanyDescription(
            blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_TAGLINE)),
            blankToNull(JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.COMPANY_DESCRIPTION))
        );
    }

    private static String resolveCompanyDescription(SurveyImportRowPayload payload) {
        String merged = mergeCompanyDescription(
            blankToNull(payload.getCompanyTagline()),
            blankToNull(payload.getCompanyIntro())
        );
        if (merged != null) {
            return merged;
        }
        return blankToNull(payload.getCompanyDescription());
    }

    private static String mergeCompanyDescription(String tagline, String intro) {
        if (!JinshujuCsvHeaders.isBlank(intro)) {
            if (!JinshujuCsvHeaders.isBlank(tagline) && !tagline.equals(intro)) {
                return tagline + "\n\n" + intro;
            }
            return intro;
        }
        return blankToNull(tagline);
    }

    private static CompanyType parseCompanyType(String raw) {
        if (JinshujuCsvHeaders.isBlank(raw)) {
            return null;
        }
        return switch (raw) {
            case "国企", "事业单位", "国企 / 事业单位" -> CompanyType.STATE;
            case "民营企业" -> CompanyType.PRIVATE;
            case "外资", "合资", "外资 / 合资" -> CompanyType.FOREIGN;
            default -> throw new BusinessException(ErrorCode.INVALID_PARAMETER, "无法识别企业类型：" + raw);
        };
    }

    private static RecruitType parseRecruitType(String raw) {
        return switch (raw) {
            case "实习" -> RecruitType.INTERN;
            case "校招", "校园招聘" -> RecruitType.CAMPUS;
            case "社招", "有经验", "社会招聘" -> RecruitType.EXPERIENCED;
            default -> throw new BusinessException(ErrorCode.INVALID_PARAMETER, "无法识别招聘类型：" + raw);
        };
    }

    private static EducationRequirement parseEducation(String raw) {
        if (JinshujuCsvHeaders.isBlank(raw) || "不限".equals(raw)) {
            return EducationRequirement.UNSPECIFIED;
        }
        return switch (raw) {
            case "本科及以上" -> EducationRequirement.BACHELOR_AND_ABOVE;
            case "硕士及以上" -> EducationRequirement.MASTER_AND_ABOVE;
            case "博士及以上" -> EducationRequirement.PHD_AND_ABOVE;
            default -> EducationRequirement.UNSPECIFIED;
        };
    }

    private static String normalizeLink(String raw) {
        String link = blankToNull(raw);
        if (link == null) {
            return null;
        }
        if (link.startsWith("http://") || link.startsWith("https://")) {
            return link;
        }
        return "https://" + link;
    }

    private static LocalDateTime parseDeadline(String raw) {
        if (JinshujuCsvHeaders.isBlank(raw)) {
            return null;
        }
        for (DateTimeFormatter fmt : DEADLINE_DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(raw, fmt);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }
        for (DateTimeFormatter fmt : DEADLINE_DATE_FORMATS) {
            try {
                return LocalDate.parse(raw, fmt).atTime(23, 59, 59);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }
        throw new BusinessException(ErrorCode.INVALID_PARAMETER, "无法解析投递截止日期：" + raw);
    }

    private static String blankToNull(String value) {
        if (JinshujuCsvHeaders.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    private static boolean isEmptyRow(String[] values) {
        if (values == null || values.length == 0) {
            return true;
        }
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return false;
            }
        }
        return true;
    }

    private static String resolveRowLabel(int rowNumber, Map<String, String> row) {
        String rowLabel = JinshujuCsvHeaders.get(row, JinshujuCsvHeaders.ROW_NO);
        return resolveRowLabel(rowNumber, rowLabel);
    }

    private static String resolveRowLabel(int rowNumber, String rowLabel) {
        if (JinshujuCsvHeaders.isBlank(rowLabel)) {
            return String.valueOf(rowNumber);
        }
        return rowLabel.trim();
    }

    private static SurveyImportPreviewRow invalidPreview(
        int rowNumber,
        String rowLabel,
        String infoType,
        String companyName,
        String companyTypeRaw,
        String errorMessage,
        SurveyImportRowPayload payload
    ) {
        return SurveyImportPreviewRow.builder()
            .rowNumber(rowNumber)
            .rowLabel(rowLabel)
            .valid(false)
            .infoType(infoType)
            .companyName(companyName)
            .companyType(companyTypeRaw)
            .plannedAction("INVALID")
            .errorMessage(errorMessage)
            .summary(errorMessage)
            .payload(payload)
            .build();
    }

    private static SurveyImportRowResult fail(int rowNumber, String rowLabel, String message) {
        return SurveyImportRowResult.builder()
            .rowNumber(rowNumber)
            .rowLabel(rowLabel)
            .success(false)
            .message(message)
            .build();
    }

    private record ParsedCsv(String fileName, String[] header, List<String[]> rows) {
    }

    private record CompanyUpsertResult(Company company, boolean created) {
    }
}
