package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.ActivitySubscriptionRepository;
import cn.edu.zju.cs.jobmate.services.ActivitySubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for ActivitySubscription entity.
 */
@Service
@Transactional
public class ActivitySubscriptionServiceImpl implements ActivitySubscriptionService {

    private final ActivitySubscriptionRepository activitySubscriptionRepository;

    public ActivitySubscriptionServiceImpl(ActivitySubscriptionRepository activitySubscriptionRepository) {
        this.activitySubscriptionRepository = activitySubscriptionRepository;
    }

    @Override
    @SuppressWarnings("null")
    public ActivitySubscription create(ActivitySubscription activitySubscription) {
        return activitySubscriptionRepository.save(activitySubscription);
    }

    @Override
    public ActivitySubscription create(User user, ActivityInfo activityInfo) {
        if (activitySubscriptionRepository.existsByUserAndActivityInfo(user, activityInfo)) {
            Integer userId = user != null ? user.getId() : null;
            Integer activityInfoId = activityInfo != null ? activityInfo.getId() : null;
            throw new IllegalArgumentException("ActivitySubscription already exists for user " + userId + " and activity info " + activityInfoId);
        }
        return activitySubscriptionRepository.save(new ActivitySubscription(user, activityInfo));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActivitySubscription> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return activitySubscriptionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivitySubscription> getAll() {
        return activitySubscriptionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivitySubscription> getAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return activitySubscriptionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivitySubscription> getByUser(User user) {
        return activitySubscriptionRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivitySubscription> getByUserId(Integer userId) {
        return activitySubscriptionRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActivitySubscription> getByUserAndActivityInfo(User user, ActivityInfo activityInfo) {
        return activitySubscriptionRepository.findByUserAndActivityInfo(user, activityInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActivitySubscription> getByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId) {
        return activitySubscriptionRepository.findByUserIdAndActivityInfoId(userId, activityInfoId);
    }

    @Override
    public ActivitySubscription update(ActivitySubscription activitySubscription) {
        Integer id = activitySubscription.getId();
        if (id == null || !activitySubscriptionRepository.existsById(id)) {
            throw new IllegalArgumentException("ActivitySubscription with id " + id + " does not exist");
        }
        return activitySubscriptionRepository.save(activitySubscription);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            activitySubscriptionRepository.deleteById(id);
        }
    }

    @Override
    public void deleteByUserAndActivityInfo(User user, ActivityInfo activityInfo) {
        Optional<ActivitySubscription> subscription = activitySubscriptionRepository.findByUserAndActivityInfo(user, activityInfo);
        subscription.ifPresent(activitySubscriptionRepository::delete);
    }

    @Override
    public void deleteByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId) {
        Optional<ActivitySubscription> subscription = activitySubscriptionRepository.findByUserIdAndActivityInfoId(userId, activityInfoId);
        subscription.ifPresent(activitySubscriptionRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return id != null && activitySubscriptionRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserAndActivityInfo(User user, ActivityInfo activityInfo) {
        return activitySubscriptionRepository.existsByUserAndActivityInfo(user, activityInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId) {
        return activitySubscriptionRepository.existsByUserIdAndActivityInfoId(userId, activityInfoId);
    }
}

