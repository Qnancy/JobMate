package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.dto.job.*;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import cn.edu.zju.cs.jobmate.utils.query.QuerySpecBuilder;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class JobInfoServiceImpl implements JobInfoService {

    private final JobInfoRepository jobInfoRepository;
    private final CompanyService companyService;

    public JobInfoServiceImpl(
        JobInfoRepository jobInfoRepository,
        CompanyService companyService
    ) {
        this.jobInfoRepository = jobInfoRepository;
        this.companyService = companyService;
    }

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
    public void delete(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        jobInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public JobInfo update(Integer id, JobInfoUpdateRequest dto) {
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
    public JobInfo getById(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        return jobInfoRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.JOB_INFO_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> getAll(cn.edu.zju.cs.jobmate.dto.common.PageRequest dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        return jobInfoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> query(JobInfoQueryRequest dto) {
        Specification<JobInfo> spec = QuerySpecBuilder.build(
            dto.getKeyword(),
            QuerySpecBuilder.Fields.of("company.name", "position", "city", "location"),
            QuerySpecBuilder.Filter.of("recruitType", dto.getRecruitType())
        );
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        return jobInfoRepository.findAll(spec, pageable);
    }
}
