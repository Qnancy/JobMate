package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import cn.edu.zju.cs.jobmate.models.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for JobSubscription entity.
 */
public interface JobSubscriptionService {

    /**
     * Create a new job subscription.
     *
     * @param jobSubscription job subscription to create
     * @return created job subscription
     */
    JobSubscription create(JobSubscription jobSubscription);

    /**
     * Create a new job subscription by user and job info.
     *
     * @param user user entity
     * @param jobInfo job info entity
     * @return created job subscription
     */
    JobSubscription create(User user, JobInfo jobInfo);

    /**
     * Get job subscription by id.
     *
     * @param id job subscription id
     * @return optional job subscription
     */
    Optional<JobSubscription> getById(Integer id);

    /**
     * Get all job subscriptions.
     *
     * @return list of job subscriptions
     */
    List<JobSubscription> getAll();

    /**
     * Get all job subscriptions with pagination.
     *
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of job subscriptions
     */
    Page<JobSubscription> getAll(Integer page, Integer pageSize);

    /**
     * Get job subscriptions by user.
     *
     * @param user user entity
     * @return list of job subscriptions
     */
    List<JobSubscription> getByUser(User user);

    /**
     * Get job subscriptions by user id.
     *
     * @param userId user id
     * @return list of job subscriptions
     */
    List<JobSubscription> getByUserId(Integer userId);

    /**
     * Get job subscription by user and job info.
     *
     * @param user user entity
     * @param jobInfo job info entity
     * @return optional job subscription
     */
    Optional<JobSubscription> getByUserAndJobInfo(User user, JobInfo jobInfo);

    /**
     * Get job subscription by user id and job info id.
     *
     * @param userId user id
     * @param jobInfoId job info id
     * @return optional job subscription
     */
    Optional<JobSubscription> getByUserIdAndJobInfoId(Integer userId, Integer jobInfoId);

    /**
     * Update job subscription.
     *
     * @param jobSubscription job subscription to update
     * @return updated job subscription
     */
    JobSubscription update(JobSubscription jobSubscription);

    /**
     * Delete job subscription by id.
     *
     * @param id job subscription id
     */
    void deleteById(Integer id);

    /**
     * Delete job subscription by user and job info.
     *
     * @param user user entity
     * @param jobInfo job info entity
     */
    void deleteByUserAndJobInfo(User user, JobInfo jobInfo);

    /**
     * Delete job subscription by user id and job info id.
     *
     * @param userId user id
     * @param jobInfoId job info id
     */
    void deleteByUserIdAndJobInfoId(Integer userId, Integer jobInfoId);

    /**
     * Check if job subscription exists by id.
     *
     * @param id job subscription id
     * @return true if exists
     */
    boolean existsById(Integer id);

    /**
     * Check if job subscription exists by user and job info.
     *
     * @param user user entity
     * @param jobInfo job info entity
     * @return true if exists
     */
    boolean existsByUserAndJobInfo(User user, JobInfo jobInfo);

    /**
     * Check if job subscription exists by user id and job info id.
     *
     * @param userId user id
     * @param jobInfoId job info id
     * @return true if exists
     */
    boolean existsByUserIdAndJobInfoId(Integer userId, Integer jobInfoId);
}

