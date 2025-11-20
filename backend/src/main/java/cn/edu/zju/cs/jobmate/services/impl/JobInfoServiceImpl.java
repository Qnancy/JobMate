package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for JobInfo entity.
 */
@Service
@Transactional
public class JobInfoServiceImpl implements JobInfoService {

    private final JobInfoRepository jobInfoRepository;

    public JobInfoServiceImpl(JobInfoRepository jobInfoRepository) {
        this.jobInfoRepository = jobInfoRepository;
    }

    @Override
    @SuppressWarnings("null")
    public JobInfo create(JobInfo jobInfo) {
        return jobInfoRepository.save(jobInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JobInfo> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return jobInfoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobInfo> getAll() {
        return jobInfoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> getAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return jobInfoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobInfo> getByCompany(Company company) {
        return jobInfoRepository.findByCompany(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobInfo> getByCompanyId(Integer companyId) {
        return jobInfoRepository.findByCompanyId(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> getByCompany(Company company, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return jobInfoRepository.findByCompany(company, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> getByCompanyId(Integer companyId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return jobInfoRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobInfo> getByRecruitType(RecruitType recruitType) {
        return jobInfoRepository.findByRecruitType(recruitType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> getByRecruitType(RecruitType recruitType, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return jobInfoRepository.findByRecruitType(recruitType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobInfo> getByCity(String city) {
        return jobInfoRepository.findByCity(city);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> getByCity(String city, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return jobInfoRepository.findByCity(city, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobInfo> getByCompanyAndRecruitType(Company company, RecruitType recruitType) {
        return jobInfoRepository.findByCompanyAndRecruitType(company, recruitType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> getByCompanyAndRecruitType(Company company, RecruitType recruitType, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return jobInfoRepository.findByCompanyAndRecruitType(company, recruitType, pageable);
    }

    @Override
    public JobInfo update(JobInfo jobInfo) {
        Integer id = jobInfo.getId();
        if (id == null || !jobInfoRepository.existsById(id)) {
            throw new IllegalArgumentException("JobInfo with id " + id + " does not exist");
        }
        return jobInfoRepository.save(jobInfo);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            jobInfoRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return id != null && jobInfoRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobInfo> query(String keyword, RecruitType recruitType, Integer page, Integer pageSize) {
        Specification<JobInfo> spec = buildSpecification(keyword, recruitType);
        Pageable pageable = PageRequest.of(
            page != null ? page : 0,
            pageSize != null ? pageSize : 10
        );
        return jobInfoRepository.findAll(spec, pageable);
    }

    private Specification<JobInfo> buildSpecification(String keyword, RecruitType recruitType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword search: company_name, position, location
            if (StringUtils.hasText(keyword)) {
                String keywordPattern = "%" + keyword.trim() + "%";
                Join<JobInfo, Company> companyJoin = root.join("company", JoinType.LEFT);
                Predicate companyNamePredicate = cb.like(cb.lower(companyJoin.get("name")), keywordPattern.toLowerCase());
                Predicate positionPredicate = cb.like(cb.lower(root.get("position")), keywordPattern.toLowerCase());
                Predicate locationPredicate = cb.like(cb.lower(root.get("location")), keywordPattern.toLowerCase());
                predicates.add(cb.or(companyNamePredicate, positionPredicate, locationPredicate));
            }

            // Recruit type filter
            if (recruitType != null) {
                predicates.add(cb.equal(root.get("recruitType"), recruitType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

