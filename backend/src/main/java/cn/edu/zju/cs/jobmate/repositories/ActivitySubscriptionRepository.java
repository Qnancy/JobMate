package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.ActivitySubscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO for {@link ActivitySubscription}.
 */
@Repository
public interface ActivitySubscriptionRepository extends JpaRepository<ActivitySubscription, Integer> {

    /**
     * Find activity subscriptions by user ID with pagination.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return a page of activity subscriptions
     */
    Page<ActivitySubscription> findByUserId(Integer userId, Pageable pageable);
}
