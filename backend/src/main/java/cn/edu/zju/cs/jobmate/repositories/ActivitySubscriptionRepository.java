package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import cn.edu.zju.cs.jobmate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ActivitySubscription entity.
 */
@Repository
public interface ActivitySubscriptionRepository extends JpaRepository<ActivitySubscription, Integer> {

    /**
     * Find activity subscriptions by user.
     *
     * @param user user entity
     * @return list of activity subscriptions
     */
    List<ActivitySubscription> findByUser(User user);

    /**
     * Find activity subscriptions by user id.
     *
     * @param userId user id
     * @return list of activity subscriptions
     */
    List<ActivitySubscription> findByUserId(Integer userId);

    /**
     * Find activity subscription by user and activity info.
     *
     * @param user user entity
     * @param activityInfo activity info entity
     * @return optional activity subscription
     */
    Optional<ActivitySubscription> findByUserAndActivityInfo(User user, ActivityInfo activityInfo);

    /**
     * Find activity subscription by user id and activity info id.
     *
     * @param userId user id
     * @param activityInfoId activity info id
     * @return optional activity subscription
     */
    Optional<ActivitySubscription> findByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId);

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

