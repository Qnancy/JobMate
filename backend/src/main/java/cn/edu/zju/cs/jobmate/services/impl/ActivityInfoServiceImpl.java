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
import cn.edu.zju.cs.jobmate.utils.query.QuerySpecBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> query(ActivityInfoQueryRequest dto) {
        Specification<ActivityInfo> spec = QuerySpecBuilder.build(
            dto.getKeyword(),
            QuerySpecBuilder.Fields.of("company.name", "title", "location")
        );
        return activityInfoRepository.findAll(spec, dto.toPageable());
    }
}
