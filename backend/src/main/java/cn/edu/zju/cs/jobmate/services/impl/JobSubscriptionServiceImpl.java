package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.JobSubscriptionRepository;
import cn.edu.zju.cs.jobmate.services.JobSubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for JobSubscription entity.
 */
@Service
@Transactional
public class JobSubscriptionServiceImpl implements JobSubscriptionService {

    private final JobSubscriptionRepository jobSubscriptionRepository;

    public JobSubscriptionServiceImpl(JobSubscriptionRepository jobSubscriptionRepository) {
        this.jobSubscriptionRepository = jobSubscriptionRepository;
    }

    @Override
    @SuppressWarnings("null")
    public JobSubscription create(JobSubscription jobSubscription) {
        return jobSubscriptionRepository.save(jobSubscription);
    }

    @Override
    public JobSubscription create(User user, JobInfo jobInfo) {
        if (jobSubscriptionRepository.existsByUserAndJobInfo(user, jobInfo)) {
            Integer userId = user != null ? user.getId() : null;
            Integer jobInfoId = jobInfo != null ? jobInfo.getId() : null;
            throw new IllegalArgumentException("JobSubscription already exists for user " + userId + " and job info " + jobInfoId);
        }
        return jobSubscriptionRepository.save(new JobSubscription(user, jobInfo));
    }

    @Override
    public Optional<JobSubscription> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return jobSubscriptionRepository.findById(id);
    }

    @Override
    public List<JobSubscription> getAll() {
        return jobSubscriptionRepository.findAll();
    }

    @Override
    public Page<JobSubscription> getAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return jobSubscriptionRepository.findAll(pageable);
    }

    @Override
    public List<JobSubscription> getByUser(User user) {
        return jobSubscriptionRepository.findByUser(user);
    }

    @Override
    public List<JobSubscription> getByUserId(Integer userId) {
        return jobSubscriptionRepository.findByUserId(userId);
    }

    @Override
    public Optional<JobSubscription> getByUserAndJobInfo(User user, JobInfo jobInfo) {
        return jobSubscriptionRepository.findByUserAndJobInfo(user, jobInfo);
    }

    @Override
    public Optional<JobSubscription> getByUserIdAndJobInfoId(Integer userId, Integer jobInfoId) {
        return jobSubscriptionRepository.findByUserIdAndJobInfoId(userId, jobInfoId);
    }

    @Override
    public JobSubscription update(JobSubscription jobSubscription) {
        Integer id = jobSubscription.getId();
        if (id == null || !jobSubscriptionRepository.existsById(id)) {
            throw new IllegalArgumentException("JobSubscription with id " + id + " does not exist");
        }
        return jobSubscriptionRepository.save(jobSubscription);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            jobSubscriptionRepository.deleteById(id);
        }
    }

    @Override
    public void deleteByUserAndJobInfo(User user, JobInfo jobInfo) {
        Optional<JobSubscription> subscription = jobSubscriptionRepository.findByUserAndJobInfo(user, jobInfo);
        subscription.ifPresent(jobSubscriptionRepository::delete);
    }

    @Override
    public void deleteByUserIdAndJobInfoId(Integer userId, Integer jobInfoId) {
        Optional<JobSubscription> subscription = jobSubscriptionRepository.findByUserIdAndJobInfoId(userId, jobInfoId);
        subscription.ifPresent(jobSubscriptionRepository::delete);
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && jobSubscriptionRepository.existsById(id);
    }

    @Override
    public boolean existsByUserAndJobInfo(User user, JobInfo jobInfo) {
        return jobSubscriptionRepository.existsByUserAndJobInfo(user, jobInfo);
    }

    @Override
    public boolean existsByUserIdAndJobInfoId(Integer userId, Integer jobInfoId) {
        return jobSubscriptionRepository.existsByUserIdAndJobInfoId(userId, jobInfoId);
    }
}

