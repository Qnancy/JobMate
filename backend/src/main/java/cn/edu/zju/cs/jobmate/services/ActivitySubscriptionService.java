package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import cn.edu.zju.cs.jobmate.models.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for ActivitySubscription entity.
 */
public interface ActivitySubscriptionService {

    /**
     * Create a new activity subscription.
     *
     * @param activitySubscription activity subscription to create
     * @return created activity subscription
     */
    ActivitySubscription create(ActivitySubscription activitySubscription);

    /**
     * Create a new activity subscription by user and activity info.
     *
     * @param user user entity
     * @param activityInfo activity info entity
     * @return created activity subscription
     */
    ActivitySubscription create(User user, ActivityInfo activityInfo);

    /**
     * Get activity subscription by id.
     *
     * @param id activity subscription id
     * @return optional activity subscription
     */
    Optional<ActivitySubscription> getById(Integer id);

    /**
     * Get all activity subscriptions.
     *
     * @return list of activity subscriptions
     */
    List<ActivitySubscription> getAll();

    /**
     * Get all activity subscriptions with pagination.
     *
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of activity subscriptions
     */
    Page<ActivitySubscription> getAll(Integer page, Integer pageSize);

    /**
     * Get activity subscriptions by user.
     *
     * @param user user entity
     * @return list of activity subscriptions
     */
    List<ActivitySubscription> getByUser(User user);

    /**
     * Get activity subscriptions by user id.
     *
     * @param userId user id
     * @return list of activity subscriptions
     */
    List<ActivitySubscription> getByUserId(Integer userId);

    /**
     * Get activity subscription by user and activity info.
     *
     * @param user user entity
     * @param activityInfo activity info entity
     * @return optional activity subscription
     */
    Optional<ActivitySubscription> getByUserAndActivityInfo(User user, ActivityInfo activityInfo);

    /**
     * Get activity subscription by user id and activity info id.
     *
     * @param userId user id
     * @param activityInfoId activity info id
     * @return optional activity subscription
     */
    Optional<ActivitySubscription> getByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId);

    /**
     * Update activity subscription.
     *
     * @param activitySubscription activity subscription to update
     * @return updated activity subscription
     */
    ActivitySubscription update(ActivitySubscription activitySubscription);

    /**
     * Delete activity subscription by id.
     *
     * @param id activity subscription id
     */
    void deleteById(Integer id);

    /**
     * Delete activity subscription by user and activity info.
     *
     * @param user user entity
     * @param activityInfo activity info entity
     */
    void deleteByUserAndActivityInfo(User user, ActivityInfo activityInfo);

    /**
     * Delete activity subscription by user id and activity info id.
     *
     * @param userId user id
     * @param activityInfoId activity info id
     */
    void deleteByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId);

    /**
     * Check if activity subscription exists by id.
     *
     * @param id activity subscription id
     * @return true if exists
     */
    boolean existsById(Integer id);

    /**
     * Check if activity subscription exists by user and activity info.
     *
     * @param user user entity
     * @param activityInfo activity info entity
     * @return true if exists
     */
    boolean existsByUserAndActivityInfo(User user, ActivityInfo activityInfo);

    /**
     * Check if activity subscription exists by user id and activity info id.
     *
     * @param userId user id
     * @param activityInfoId activity info id
     * @return true if exists
     */
    boolean existsByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId);
}

