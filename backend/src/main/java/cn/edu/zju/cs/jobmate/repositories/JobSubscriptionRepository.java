package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.JobSubscription;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO for {@link JobSubscription}.
 */
@Repository
public interface JobSubscriptionRepository extends JpaRepository<JobSubscription, Integer> {

    /**
     * Find job subscriptions by user ID with pagination.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return a page of job subscriptions
     */
    Page<JobSubscription> findByUserId(Integer userId, Pageable pageable);
}
