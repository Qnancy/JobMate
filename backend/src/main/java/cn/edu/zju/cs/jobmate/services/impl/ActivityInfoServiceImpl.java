package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.dto.PageResponse;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public PageResponse<ActivityInfo> getAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityInfo> pageResult = activityInfoRepository.findAll(pageable);
        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                page,
                pageSize
        );
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
    public PageResponse<ActivityInfo> getByCompany(Company company, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityInfo> pageResult = activityInfoRepository.findByCompany(company, pageable);
        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                page,
                pageSize
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityInfo> getByCompanyId(Integer companyId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityInfo> pageResult = activityInfoRepository.findByCompanyId(companyId, pageable);
        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                page,
                pageSize
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByCity(String city) {
        return activityInfoRepository.findByCity(city);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityInfo> getByCity(String city, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityInfo> pageResult = activityInfoRepository.findByCity(city, pageable);
        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                page,
                pageSize
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByTimeAfter(LocalDateTime time) {
        return activityInfoRepository.findByTimeAfter(time);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityInfo> getByTimeAfter(LocalDateTime time, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityInfo> pageResult = activityInfoRepository.findByTimeAfter(time, pageable);
        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                page,
                pageSize
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByTimeBefore(LocalDateTime time) {
        return activityInfoRepository.findByTimeBefore(time);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityInfo> getByTimeBefore(LocalDateTime time, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityInfo> pageResult = activityInfoRepository.findByTimeBefore(time, pageable);
        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                page,
                pageSize
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityInfo> getByTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return activityInfoRepository.findByTimeBetween(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityInfo> getByTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityInfo> pageResult = activityInfoRepository.findByTimeBetween(startTime, endTime, pageable);
        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                page,
                pageSize
        );
    }

    @Override
    public ActivityInfo update(ActivityInfo activityInfo) {
        Integer id = activityInfo.getId();
        if (id == null || !activityInfoRepository.existsById(id)) {
            throw new IllegalArgumentException("ActivityInfo with id " + id + " does not exist");
        }
        return activityInfoRepository.save(activityInfo);
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
}

