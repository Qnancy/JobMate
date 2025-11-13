package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.Subsription;
import cn.edu.zju.cs.jobmate.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SubscriptionRepository to verify unique constraints.
 * 
 * This test demonstrates that:
 * 1. Without unique constraints: duplicate subscriptions are allowed (BAD)
 * 2. With unique constraints: duplicate subscriptions are prevented (GOOD)
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Subscription Repository Tests - Unique Constraint Validation")
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ActivityInfoRepository activityInfoRepository;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser;
    private Company testCompany;
    private ActivityInfo testActivityInfo;
    private JobInfo testJobInfo;
    private ActivityInfo anotherActivityInfo;
    private JobInfo anotherJobInfo;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User("testuser", "password123", UserRole.USER);
        testUser = userRepository.save(testUser);

        // Create test company
        testCompany = new Company("Test Company", CompanyType.PRIVATE);
        testCompany = companyRepository.save(testCompany);

        // Create test activity info
        testActivityInfo = new ActivityInfo(
            testCompany,
            "Test Activity",
            LocalDateTime.now().plusDays(7)
        );
        testActivityInfo = activityInfoRepository.save(testActivityInfo);

        // Create another activity info
        anotherActivityInfo = new ActivityInfo(
            testCompany,
            "Another Activity",
            LocalDateTime.now().plusDays(14)
        );
        anotherActivityInfo = activityInfoRepository.save(anotherActivityInfo);

        // Create test job info
        testJobInfo = new JobInfo(
            testCompany,
            RecruitType.CAMPUS,
            "Software Engineer",
            "https://example.com/job1"
        );
        testJobInfo = jobInfoRepository.save(testJobInfo);

        // Create another job info
        anotherJobInfo = new JobInfo(
            testCompany,
            RecruitType.INTERN,
            "Intern Developer",
            "https://example.com/job2"
        );
        anotherJobInfo = jobInfoRepository.save(anotherJobInfo);
    }

    @Test
    @DisplayName("Should prevent duplicate subscription to the same Activity")
    @Transactional
    void testDuplicateActivitySubscriptionShouldFail() {
        // First subscription should succeed
        Subsription firstSubscription = new Subsription(testUser, testActivityInfo);
        Subsription saved = subscriptionRepository.save(firstSubscription);
        entityManager.flush();
        assertNotNull(saved.getId());
        assertEquals(1, subscriptionRepository.count());

        // Attempt to create duplicate subscription - should fail
        Subsription duplicateSubscription = new Subsription(testUser, testActivityInfo);
        
        // This should throw DataIntegrityViolationException due to unique constraint
        assertThrows(DataIntegrityViolationException.class, () -> {
            subscriptionRepository.save(duplicateSubscription);
            entityManager.flush();
        }, "Duplicate subscription to the same activity should be prevented by unique constraint");

        // Clear the persistence context to remove the failed entity
        entityManager.clear();
        
        // Verify only one subscription exists
        assertEquals(1, subscriptionRepository.count());
    }

    @Test
    @DisplayName("Should prevent duplicate subscription to the same Job")
    @Transactional
    void testDuplicateJobSubscriptionShouldFail() {
        // First subscription should succeed
        Subsription firstSubscription = new Subsription(testUser, testJobInfo);
        Subsription saved = subscriptionRepository.save(firstSubscription);
        entityManager.flush();
        assertNotNull(saved.getId());
        assertEquals(1, subscriptionRepository.count());

        // Attempt to create duplicate subscription - should fail
        Subsription duplicateSubscription = new Subsription(testUser, testJobInfo);
        
        // This should throw DataIntegrityViolationException due to unique constraint
        assertThrows(DataIntegrityViolationException.class, () -> {
            subscriptionRepository.save(duplicateSubscription);
            entityManager.flush();
        }, "Duplicate subscription to the same job should be prevented by unique constraint");

        // Clear the persistence context to remove the failed entity
        entityManager.clear();
        
        // Verify only one subscription exists
        assertEquals(1, subscriptionRepository.count());
    }

    @Test
    @DisplayName("Should allow subscription to different Activities")
    void testDifferentActivitySubscriptionsShouldSucceed() {
        // Subscribe to first activity
        Subsription subscription1 = new Subsription(testUser, testActivityInfo);
        subscriptionRepository.save(subscription1);

        // Subscribe to different activity - should succeed
        Subsription subscription2 = new Subsription(testUser, anotherActivityInfo);
        subscriptionRepository.save(subscription2);

        // Verify both subscriptions exist
        assertEquals(2, subscriptionRepository.count());
        assertTrue(subscriptionRepository.existsByUserAndActivityInfo(testUser, testActivityInfo));
        assertTrue(subscriptionRepository.existsByUserAndActivityInfo(testUser, anotherActivityInfo));
    }

    @Test
    @DisplayName("Should allow subscription to different Jobs")
    void testDifferentJobSubscriptionsShouldSucceed() {
        // Subscribe to first job
        Subsription subscription1 = new Subsription(testUser, testJobInfo);
        subscriptionRepository.save(subscription1);

        // Subscribe to different job - should succeed
        Subsription subscription2 = new Subsription(testUser, anotherJobInfo);
        subscriptionRepository.save(subscription2);

        // Verify both subscriptions exist
        assertEquals(2, subscriptionRepository.count());
        assertTrue(subscriptionRepository.existsByUserAndJobInfo(testUser, testJobInfo));
        assertTrue(subscriptionRepository.existsByUserAndJobInfo(testUser, anotherJobInfo));
    }

    @Test
    @DisplayName("Should allow same user to subscribe to both Activity and Job")
    void testUserCanSubscribeToBothActivityAndJob() {
        // Subscribe to activity
        Subsription activitySubscription = new Subsription(testUser, testActivityInfo);
        subscriptionRepository.save(activitySubscription);

        // Subscribe to job - should succeed (different info types)
        Subsription jobSubscription = new Subsription(testUser, testJobInfo);
        subscriptionRepository.save(jobSubscription);

        // Verify both subscriptions exist
        assertEquals(2, subscriptionRepository.count());
        assertTrue(subscriptionRepository.existsByUserAndActivityInfo(testUser, testActivityInfo));
        assertTrue(subscriptionRepository.existsByUserAndJobInfo(testUser, testJobInfo));
    }

    @Test
    @DisplayName("Should allow different users to subscribe to the same Activity")
    void testDifferentUsersCanSubscribeToSameActivity() {
        // Create another user
        User anotherUser = new User("anotheruser", "password123", UserRole.USER);
        anotherUser = userRepository.save(anotherUser);

        // First user subscribes
        Subsription subscription1 = new Subsription(testUser, testActivityInfo);
        subscriptionRepository.save(subscription1);

        // Second user subscribes to same activity - should succeed
        Subsription subscription2 = new Subsription(anotherUser, testActivityInfo);
        subscriptionRepository.save(subscription2);

        // Verify both subscriptions exist
        assertEquals(2, subscriptionRepository.count());
        assertTrue(subscriptionRepository.existsByUserAndActivityInfo(testUser, testActivityInfo));
        assertTrue(subscriptionRepository.existsByUserAndActivityInfo(anotherUser, testActivityInfo));
    }

    @Test
    @DisplayName("Should allow different users to subscribe to the same Job")
    void testDifferentUsersCanSubscribeToSameJob() {
        // Create another user
        User anotherUser = new User("anotheruser2", "password123", UserRole.USER);
        anotherUser = userRepository.save(anotherUser);

        // First user subscribes
        Subsription subscription1 = new Subsription(testUser, testJobInfo);
        subscriptionRepository.save(subscription1);

        // Second user subscribes to same job - should succeed
        Subsription subscription2 = new Subsription(anotherUser, testJobInfo);
        subscriptionRepository.save(subscription2);

        // Verify both subscriptions exist
        assertEquals(2, subscriptionRepository.count());
        assertTrue(subscriptionRepository.existsByUserAndJobInfo(testUser, testJobInfo));
        assertTrue(subscriptionRepository.existsByUserAndJobInfo(anotherUser, testJobInfo));
    }
}

