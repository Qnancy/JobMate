package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.dto.subscription.ActivitySubscriptionCreateRequest;
import cn.edu.zju.cs.jobmate.dto.subscription.ActivitySubscriptionResponse;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;
import cn.edu.zju.cs.jobmate.services.ActivitySubscriptionService;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ActivitySubscription REST Controller
 * 
 * Provides RESTful APIs for activity subscription management operations
 * 
 * @author JobMate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/activitysubscriptions")
@CrossOrigin(originPatterns = "*")
@Validated
public class ActivitySubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(ActivitySubscriptionController.class);

    private final ActivitySubscriptionService activitySubscriptionService;
    private final UserService userService;
    private final ActivityInfoService activityInfoService;

    /**
     * Constructor injection for services
     */
    @Autowired
    public ActivitySubscriptionController(ActivitySubscriptionService activitySubscriptionService, 
                                        UserService userService, ActivityInfoService activityInfoService) {
        this.activitySubscriptionService = activitySubscriptionService;
        this.userService = userService;
        this.activityInfoService = activityInfoService;
    }

    /**
     * Creates a new activity subscription
     * POST /api/activitysubscriptions
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ActivitySubscriptionResponse>> createActivitySubscription(
            @Valid @RequestBody ActivitySubscriptionCreateRequest request) {
        
        logger.info("Creating activity subscription for user: {}, activityInfo: {}", 
                   request.getUser().getId(), request.getActivityInfo().getId());
        
        // Validate user and activity info exist using new service methods
        User user = userService.getUserById(request.getUser().getId());
        ActivityInfo activityInfo = activityInfoService.getActivityInfoById(request.getActivityInfo().getId());
        
        // Check if subscription already exists
        if (activitySubscriptionService.existsByUserIdAndActivityInfoId(user.getId(), activityInfo.getId())) {
            throw new BusinessException(ErrorCode.ACTIVITY_SUBSCRIPTION_ALREADY_EXISTS);
        }
        
        // Create ActivitySubscription
        ActivitySubscription activitySubscription = activitySubscriptionService.create(user, activityInfo);
        ActivitySubscriptionResponse response = ActivitySubscriptionResponse.from(activitySubscription);
        
        logger.info("Successfully created activity subscription with ID: {}", activitySubscription.getId());
        return ResponseEntity.ok(ApiResponse.ok("宣讲会订阅创建成功", response));
    }

    /**
     * Delete activity subscription
     * DELETE /api/activitysubscriptions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActivitySubscription(@PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Deleting activity subscription with ID: {}", id);
        
        if (!activitySubscriptionService.existsById(id)) {
            throw new BusinessException(ErrorCode.ACTIVITY_SUBSCRIPTION_NOT_FOUND);
        }
        
        activitySubscriptionService.deleteById(id);
        logger.info("Successfully deleted activity subscription with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }

    /**
     * Retrieves activity subscriptions with pagination
     * GET /api/activitysubscriptions
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ActivitySubscriptionResponse>>> getAllActivitySubscriptions(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        
        logger.info("Retrieving activity subscriptions - page: {}, page_size: {}", page, pageSize);
        
        try {
            // Convert to 0-based page for Spring Data
            int springPage = page - 1;
            
            Page<ActivitySubscription> activitySubscriptionPage = activitySubscriptionService.getAll(springPage, pageSize);
            
            List<ActivitySubscriptionResponse> responses = activitySubscriptionPage.getContent().stream()
                    .map(ActivitySubscriptionResponse::from)
                    .collect(Collectors.toList());
            
            PageResponse<ActivitySubscriptionResponse> pageResponse = new PageResponse<>(
                responses, 
                activitySubscriptionPage.getTotalElements(),
                page,  // Return 1-based page to frontend
                pageSize
            );
            
            logger.info("Successfully retrieved {} activity subscriptions", responses.size());
            return ResponseEntity.ok(ApiResponse.ok("查询成功", pageResponse));
            
        } catch (Exception e) {
            logger.error("Error retrieving activity subscriptions: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}