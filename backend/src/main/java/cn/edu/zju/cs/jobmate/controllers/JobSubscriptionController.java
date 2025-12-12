package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.dto.subscription.JobSubscriptionCreateRequest;
import cn.edu.zju.cs.jobmate.dto.subscription.JobSubscriptionResponse;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import cn.edu.zju.cs.jobmate.services.JobSubscriptionService;
import cn.edu.zju.cs.jobmate.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JobSubscription REST Controller
 * 
 * Provides RESTful APIs for job subscription management operations
 * 
 * @author JobMate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/jobsubscriptions")
@CrossOrigin(originPatterns = "*")
@Validated
public class JobSubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(JobSubscriptionController.class);

    private final JobSubscriptionService jobSubscriptionService;
    private final UserService userService;
    private final JobInfoService jobInfoService;

    /**
     * Constructor injection for services
     */
    @Autowired
    public JobSubscriptionController(JobSubscriptionService jobSubscriptionService, 
                                   UserService userService, JobInfoService jobInfoService) {
        this.jobSubscriptionService = jobSubscriptionService;
        this.userService = userService;
        this.jobInfoService = jobInfoService;
    }

    /**
     * Creates a new job subscription
     * POST /api/jobsubscriptions
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JobSubscriptionResponse>> createJobSubscription(
            @Valid @RequestBody JobSubscriptionCreateRequest request) {
        
        logger.info("Creating job subscription for user: {}, jobInfo: {}", 
                   request.getUser().getId(), request.getJobInfo().getId());
        
        // Validate user and job info exist using new service methods
        User user = userService.getUserById(request.getUser().getId());
        JobInfo jobInfo = jobInfoService.getById(request.getJobInfo().getId());
        
        // Check if subscription already exists
        if (jobSubscriptionService.existsByUserIdAndJobInfoId(user.getId(), jobInfo.getId())) {
            throw new BusinessException(ErrorCode.JOB_SUBSCRIPTION_ALREADY_EXISTS);
        }
        
        // Create JobSubscription
        JobSubscription jobSubscription = jobSubscriptionService.create(user, jobInfo);
        JobSubscriptionResponse response = JobSubscriptionResponse.from(jobSubscription);
        
        logger.info("Successfully created job subscription with ID: {}", jobSubscription.getId());
        return ResponseEntity.ok(ApiResponse.ok("招聘订阅创建成功", response));
    }

    /**
     * Delete job subscription
     * DELETE /api/jobsubscriptions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJobSubscription(@PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Deleting job subscription with ID: {}", id);
        
        if (!jobSubscriptionService.existsById(id)) {
            throw new BusinessException(ErrorCode.JOB_SUBSCRIPTION_NOT_FOUND);
        }
        
        jobSubscriptionService.deleteById(id);
        logger.info("Successfully deleted job subscription with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }

    /**
     * Retrieves job subscriptions with pagination
     * GET /api/jobsubscriptions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobSubscriptionResponse>>> getAllJobSubscriptions(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        
        logger.info("Retrieving job subscriptions - page: {}, page_size: {}", page, pageSize);
        
        try {
            // Convert to 0-based page for Spring Data
            int springPage = page - 1;
            
            Page<JobSubscription> jobSubscriptionPage = jobSubscriptionService.getAll(springPage, pageSize);
            
            List<JobSubscriptionResponse> responses = jobSubscriptionPage.getContent().stream()
                    .map(JobSubscriptionResponse::from)
                    .collect(Collectors.toList());
            
            PageResponse<JobSubscriptionResponse> pageResponse = new PageResponse<>(
                responses, 
                jobSubscriptionPage.getTotalElements(),
                page,  // Return 1-based page to frontend
                pageSize
            );
            
            logger.info("Successfully retrieved {} job subscriptions", responses.size());
            return ResponseEntity.ok(ApiResponse.ok("查询成功", pageResponse));
            
        } catch (Exception e) {
            logger.error("Error retrieving job subscriptions: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}