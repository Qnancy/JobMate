package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.subscription.ActivitySubscriptionCreateRequest;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.ActivitySubscriptionRepository;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;
import cn.edu.zju.cs.jobmate.services.ActivitySubscriptionService;
import cn.edu.zju.cs.jobmate.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ActivitySubscription service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivitySubscriptionServiceImpl implements ActivitySubscriptionService {

    private final ActivitySubscriptionRepository activitySubscriptionRepository;
    private final ActivityInfoService activityInfoService;
    private final UserService userService;

    @Override
    @Transactional
    public ActivitySubscription create(ActivitySubscriptionCreateRequest dto) {
        // Validate ActivityInfo existence.
        ActivityInfo info = activityInfoService.getById(dto.getActivityInfoId());

        // Validate User existence.
        User user = userService.getById(dto.getUserId());

        ActivitySubscription subscription = dto.toModel();
        subscription.setInfo(info);
        subscription.setUser(user);
        return activitySubscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        activitySubscriptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivitySubscription> getAllByUser(Long uid, PageRequest dto) {
        // TODO: Validate User existence.
        return activitySubscriptionRepository.findByUserId(uid, dto.toPageable());
    }
}
