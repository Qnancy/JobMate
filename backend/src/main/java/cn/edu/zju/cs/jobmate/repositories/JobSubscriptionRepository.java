package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import cn.edu.zju.cs.jobmate.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for JobSubscription entity.
 */
@Repository
public interface JobSubscriptionRepository extends JpaRepository<JobSubscription, Integer> {

    /**
     * Find job subscriptions by user.
     *
     * @param user user entity
     * @return list of job subscriptions
     */
    List<JobSubscription> findByUser(User user);

    /**
     * Find job subscriptions by user id.
     *
     * @param userId user id
     * @return list of job subscriptions
     */
    List<JobSubscription> findByUserId(Integer userId);

    /**
     * Find job subscription by user and job info.
     *
     * @param user user entity
     * @param jobInfo job info entity
     * @return optional job subscription
     */
    Optional<JobSubscription> findByUserAndJobInfo(User user, JobInfo jobInfo);

    /**
     * Find job subscription by user id and job info id.
     *
     * @param userId user id
     * @param jobInfoId job info id
     * @return optional job subscription
     */
    Optional<JobSubscription> findByUserIdAndJobInfoId(Integer userId, Integer jobInfoId);

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

