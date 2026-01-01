package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.subscription.JobSubscriptionCreateRequest;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import org.springframework.data.domain.Page;

/**
 * JobSubscription service interface.
 */
public interface JobSubscriptionService {

    /**
     * Create a new job subscription.
     *
     * @param dto job subscription create request DTO
     * @return created job subscription
     * @throws BusinessException if the JobInfo or User does not exist
     */
    JobSubscription create(JobSubscriptionCreateRequest dto);

    /**
     * Delete job subscription by id.
     *
     * @param id job subscription id
     */
    void deleteById(Long id);

    /**
     * Get all job subscriptions by user with pagination.
     *
     * @param uid user id
     * @param dto page request DTO
     * @return page of job subscriptions
     */
    Page<JobSubscription> getAllByUser(Long uid, PageRequest dto);
}
