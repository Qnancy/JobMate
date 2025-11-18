package cn.edu.zju.cs.jobmate.services.impl;

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

