package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.dto.activity.*;
import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * ActivityInfo service implementation.
 * 
 * @see ActivityInfoService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityInfoServiceImpl implements ActivityInfoService {

    private final ActivityInfoRepository activityInfoRepository;
    private final CompanyService companyService;

    @Override
    @Transactional
    public ActivityInfo create(ActivityInfoCreateRequest dto) {
        // Validate company existence.
        Company company = companyService.getById(dto.getCompanyId());

        ActivityInfo activityInfo = dto.toModel();
        activityInfo.setCompany(company);
        return activityInfoRepository.save(activityInfo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        activityInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ActivityInfo update(Long id, ActivityInfoUpdateRequest dto) {
        // Check if no updates needed.
        if (!dto.isUpdatable()) {
            throw new BusinessException(ErrorCode.NO_UPDATES);
        }

        // Fetch existing ActivityInfo.
        ActivityInfo activityInfo = getById(id);

        // Update company.
        if (dto.getCompanyId() != null) {
            Company company = companyService.getById(dto.getCompanyId());
            activityInfo.setCompany(company);
        }

        // Update and save.
        dto.apply(activityInfo);
        return activityInfoRepository.save(Objects.requireNonNull(activityInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityInfo getById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        return activityInfoRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACTIVITY_INFO_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> getAll(PageRequest dto) {
        return activityInfoRepository.findAll(dto.toPageable());
    }

    /**
     * Empty keyword falls back to plain pagination ordered by {@code time DESC}
     * so the most recent activities surface first; non-empty keyword routes to
     * the MySQL 8 ngram FULLTEXT search on
     * {@code activity_infos(title, location, extra)} + {@code companies(name)},
     * ordered by combined BOOLEAN-mode relevance.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> query(ActivityInfoQueryRequest dto) {
        String keyword = dto.getKeyword() == null ? "" : dto.getKeyword().trim();
        Pageable basePageable = dto.toPageable();

        if (keyword.isEmpty()) {
            Pageable sorted = org.springframework.data.domain.PageRequest.of(
                basePageable.getPageNumber(),
                basePageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "time")
            );
            return activityInfoRepository.findAll(sorted);
        }

        return activityInfoRepository.searchByFulltext(keyword, basePageable);
    }
}
