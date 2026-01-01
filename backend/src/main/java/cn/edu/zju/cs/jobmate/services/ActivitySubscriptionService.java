package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.subscription.ActivitySubscriptionCreateRequest;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import org.springframework.data.domain.Page;

/**
 * ActivitySubscription service interface.
 */
public interface ActivitySubscriptionService {

    /**
     * Create a new activity subscription.
     *
     * @param dto activity subscription create request DTO
     * @return created activity subscription
     * @throws BusinessException if the ActivityInfo or User does not exist
     */
    ActivitySubscription create(ActivitySubscriptionCreateRequest dto);

    /**
     * Delete activity subscription by id.
     *
     * @param id activity subscription id
     */
    void deleteById(Long id);

    /**
     * Get all activity subscriptions by user with pagination.
     *
     * @param uid user id
     * @param dto page request DTO
     * @return page of activity subscriptions
     */
    Page<ActivitySubscription> getAllByUser(Long uid, PageRequest dto);
}
