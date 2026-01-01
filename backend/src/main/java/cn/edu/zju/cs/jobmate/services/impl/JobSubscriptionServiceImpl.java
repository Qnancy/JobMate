package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.subscription.JobSubscriptionCreateRequest;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.JobSubscriptionRepository;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import cn.edu.zju.cs.jobmate.services.JobSubscriptionService;
import cn.edu.zju.cs.jobmate.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * JobSubscription service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobSubscriptionServiceImpl implements JobSubscriptionService {

    private final JobSubscriptionRepository jobSubscriptionRepository;
    private final JobInfoService jobInfoService;
    private final UserService userService;

    @Override
    @Transactional
    public JobSubscription create(JobSubscriptionCreateRequest dto) {
        // Validate JobInfo existence.
        JobInfo info = jobInfoService.getById(dto.getJobInfoId());

        // Validate User existence.
        User user = userService.getById(dto.getUserId());

        JobSubscription subscription = dto.toModel();
        subscription.setInfo(info);
        subscription.setUser(user);
        return jobSubscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        jobSubscriptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobSubscription> getAllByUser(Long uid, PageRequest dto) {
        // TODO: Validate User existence.
        return jobSubscriptionRepository.findByUserId(uid, dto.toPageable());
    }
}
