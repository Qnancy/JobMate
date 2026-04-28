package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.job.*;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JobInfo service implementation.
 * 
 * @see JobInfoService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobInfoServiceImpl implements JobInfoService {

    private final JobInfoRepository jobInfoRepository;
    private final CompanyService companyService;

    @Override
    @Transactional
    public JobInfo create(JobInfoCreateRequest dto) {
        // Validate company existence.
        Company company = companyService.getById(dto.getCompanyId());

        JobInfo jobInfo = dto.toModel();
        jobInfo.setCompany(company);
        return jobInfoRepository.save(jobInfo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        jobInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public JobInfo update(Long id, JobInfoUpdateRequest dto) {
        // Check if no updates needed.
        if (!dto.isUpdatable()) {
            throw new BusinessException(ErrorCode.NO_UPDATES);
        }

        // Fetch existing JobInfo.
        JobInfo jobInfo = getById(id);

        // Update company.
        if (dto.getCompanyId() != null) {
            Company company = companyService.getById(dto.getCompanyId());
            jobInfo.setCompany(company);
        }

        // Update and save.
        dto.apply(jobInfo);
        return jobInfoRepository.save(Objects.requireNonNull(jobInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public JobInfo getById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        return jobInfoRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.JOB_INFO_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> getAll(PageRequest dto) {
        return jobInfoRepository.findAll(dto.toPageable());
    }

    /**
     * Routes search to the right backend depending on whether a keyword is given:
     * <ul>
     *   <li><b>Empty keyword</b> — JPA Specification with the optional
     *       {@code recruitType} filter, ordered by {@code createdAt DESC} so the
     *       latest postings surface first.</li>
     *   <li><b>Non-empty keyword</b> — MySQL 8 ngram FULLTEXT（BOOLEAN）联合
     *       对职位/地点/简介与公司名的 {@code LIKE} 子串匹配（不区分大小写），避免纯英文短词
     *       在中英混合职位名里全文匹配不到；排序仍以全文相关度为主、{@code createdAt} 为辅。</li>
     * </ul>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> query(JobInfoQueryRequest dto) {
        String keyword = dto.getKeyword() == null ? "" : dto.getKeyword().trim();
        RecruitType recruitType = dto.getRecruitType();
        Pageable basePageable = dto.toPageable();

        Long companyId = dto.getCompanyId();
        if (keyword.isEmpty()) {
            Pageable sorted = org.springframework.data.domain.PageRequest.of(
                basePageable.getPageNumber(),
                basePageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
            );
            if (companyId != null) {
                Specification<JobInfo> spec = Specification
                    .where(filterByCompanyId(companyId))
                    .and(filterByRecruitType(recruitType));
                return jobInfoRepository.findAll(spec, sorted);
            }
            return jobInfoRepository.findAll(filterByRecruitType(recruitType), sorted);
        }

        // MySQL ngram_token_size defaults to 2, so single-character tokens are
        // ignored anyway — passing the raw keyword to BOOLEAN MODE is fine.
        // LIKE 子句使用 ESCAPE '!'，避免用户输入 % / _ 破坏模式并补全文搜不到的英文子串。
        String recruitTypeName = recruitType == null ? null : recruitType.name();
        String likeKw = escapeLikeWithBangEscape(keyword);
        return jobInfoRepository.searchByFulltext(
            keyword,
            likeKw,
            recruitTypeName,
            companyId,
            basePageable
        );
    }

    /**
     * 将用户关键词转为可与 {@code LIKE ... ESCAPE '!'} 拼接的中间片段（不含两侧的 %）。
     */
    private static String escapeLikeWithBangEscape(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        return raw
            .replace("!", "!!")
            .replace("\\", "!\\")
            .replace("%", "!%")
            .replace("_", "!_");
    }

    private static Specification<JobInfo> filterByRecruitType(RecruitType recruitType) {
        return (root, query, cb) ->
            recruitType == null ? cb.conjunction() : cb.equal(root.get("recruitType"), recruitType);
    }

    private static Specification<JobInfo> filterByCompanyId(Long companyId) {
        return (root, query, cb) ->
            companyId == null ? cb.conjunction() : cb.equal(root.get("company").get("id"), companyId);
    }
}
