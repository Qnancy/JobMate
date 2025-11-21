package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for ActivityInfo entity.
 */
@Service
@Transactional
public class ActivityInfoServiceImpl implements ActivityInfoService {

    private final ActivityInfoRepository activityInfoRepository;

    public ActivityInfoServiceImpl(ActivityInfoRepository activityInfoRepository) {
        this.activityInfoRepository = activityInfoRepository;
    }

    @Override
    @SuppressWarnings("null")
    public ActivityInfo create(ActivityInfo activityInfo) {
        return activityInfoRepository.save(activityInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActivityInfo> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return activityInfoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getAll() {
        return activityInfoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> getAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return activityInfoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByCompany(Company company) {
        return activityInfoRepository.findByCompany(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByCompanyId(Integer companyId) {
        return activityInfoRepository.findByCompanyId(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> getByCompany(Company company, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return activityInfoRepository.findByCompany(company, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> getByCompanyId(Integer companyId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return activityInfoRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByCity(String city) {
        return activityInfoRepository.findByCity(city);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> getByCity(String city, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return activityInfoRepository.findByCity(city, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByTimeAfter(LocalDateTime time) {
        return activityInfoRepository.findByTimeAfter(time);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> getByTimeAfter(LocalDateTime time, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return activityInfoRepository.findByTimeAfter(time, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByTimeBefore(LocalDateTime time) {
        return activityInfoRepository.findByTimeBefore(time);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> getByTimeBefore(LocalDateTime time, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return activityInfoRepository.findByTimeBefore(time, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return activityInfoRepository.findByTimeBetween(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> getByTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return activityInfoRepository.findByTimeBetween(startTime, endTime, pageable);
    }

    @Override
    public ActivityInfo update(ActivityInfo activityInfo) {
        Integer id = activityInfo.getId();
        if (id == null || !activityInfoRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return activityInfoRepository.save(activityInfo);
    }

    @Override
    public ActivityInfo updateById(Integer id, Integer companyId, String title, LocalDateTime time, 
                                  String link, String location, String extra) {
        if (id == null || !activityInfoRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        // Use custom update query to modify specific fields
        activityInfoRepository.updateActivityInfoById(id, companyId, title, time, link, location, extra);
        
        // Return the updated activity info
        return activityInfoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Failed to retrieve updated activity info with id " + id));
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            activityInfoRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return id != null && activityInfoRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityInfo> query(String keyword, Integer page, Integer pageSize) {
        Specification<ActivityInfo> spec = buildSpecification(keyword);
        Pageable pageable = PageRequest.of(
            page != null ? page : 0,
            pageSize != null ? pageSize : 10
        );
        return activityInfoRepository.findAll(spec, pageable);
    }

    private Specification<ActivityInfo> buildSpecification(String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword search with space-separated tokens
            // Each token must appear in at least one of: title, company_name
            if (StringUtils.hasText(keyword)) {
                Join<ActivityInfo, Company> companyJoin = root.join("company", JoinType.LEFT);
                
                // Split keyword by spaces and filter out empty strings
                List<String> tokens = Arrays.stream(keyword.trim().split("\\s+"))
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());
                
                if (!tokens.isEmpty()) {
                    // For each token, create an OR condition: token in title OR company_name
                    List<Predicate> tokenPredicates = new ArrayList<>();
                    for (String token : tokens) {
                        String tokenPattern = "%" + token + "%";
                        Predicate titlePredicate = cb.like(cb.lower(root.get("title")), tokenPattern.toLowerCase());
                        Predicate companyNamePredicate = cb.like(cb.lower(companyJoin.get("name")), tokenPattern.toLowerCase());
                        // Each token must appear in at least one field
                        tokenPredicates.add(cb.or(titlePredicate, companyNamePredicate));
                    }
                    // All tokens must be matched (AND logic)
                    predicates.add(cb.and(tokenPredicates.toArray(new Predicate[0])));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

