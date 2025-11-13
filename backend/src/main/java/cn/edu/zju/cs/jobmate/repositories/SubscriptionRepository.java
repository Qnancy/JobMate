package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.Subsription;
import cn.edu.zju.cs.jobmate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Subscription entity.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subsription, Integer> {

    /**
     * Find subscriptions by user.
     *
     * @param user user entity
     * @return list of subscriptions
     */
    List<Subsription> findByUser(User user);

    /**
     * Find subscriptions by user id.
     *
     * @param userId user id
     * @return list of subscriptions
     */
    List<Subsription> findByUserId(Integer userId);

    /**
     * Find subscription by user and activity info.
     *
     * @param user user entity
     * @param activityInfo activity info entity
     * @return optional subscription
     */
    Optional<Subsription> findByUserAndActivityInfo(User user, ActivityInfo activityInfo);

    /**
     * Find subscription by user id and activity info id.
     *
     * @param userId user id
     * @param activityInfoId activity info id
     * @return optional subscription
     */
    Optional<Subsription> findByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId);

    /**
     * Find subscription by user and job info.
     *
     * @param user user entity
     * @param jobInfo job info entity
     * @return optional subscription
     */
    Optional<Subsription> findByUserAndJobInfo(User user, JobInfo jobInfo);

    /**
     * Find subscription by user id and job info id.
     *
     * @param userId user id
     * @param jobInfoId job info id
     * @return optional subscription
     */
    Optional<Subsription> findByUserIdAndJobInfoId(Integer userId, Integer jobInfoId);

    /**
     * Check if subscription exists by user and activity info.
     *
     * @param user user entity
     * @param activityInfo activity info entity
     * @return true if exists
     */
    boolean existsByUserAndActivityInfo(User user, ActivityInfo activityInfo);

    /**
     * Check if subscription exists by user id and activity info id.
     *
     * @param userId user id
     * @param activityInfoId activity info id
     * @return true if exists
     */
    boolean existsByUserIdAndActivityInfoId(Integer userId, Integer activityInfoId);

    /**
     * Check if subscription exists by user and job info.
     *
     * @param user user entity
     * @param jobInfo job info entity
     * @return true if exists
     */
    boolean existsByUserAndJobInfo(User user, JobInfo jobInfo);
    
    /**
     * Check if subscription exists by user id and job info id.
     *
     * @param userId user id
     * @param jobInfoId job info id
     * @return true if exists
     */
    boolean existsByUserIdAndJobInfoId(Integer userId, Integer jobInfoId);
}

