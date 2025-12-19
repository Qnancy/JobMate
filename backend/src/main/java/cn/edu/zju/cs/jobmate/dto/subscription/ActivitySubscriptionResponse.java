package cn.edu.zju.cs.jobmate.dto.subscription;

import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoResponse;
import cn.edu.zju.cs.jobmate.dto.user.UserResponse;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Activity subscription response DTO
 */
@Data
@Builder
public class ActivitySubscriptionResponse {
    
    private Integer id;
    private UserResponse user;
    private ActivityInfoResponse activityInfo;
    
    private LocalDateTime subscribedAt;

    /**
     * Convert from ActivitySubscription entity to ActivitySubscriptionResponse
     */
    public static ActivitySubscriptionResponse from(ActivitySubscription activitySubscription) {
        if (activitySubscription == null) {
            return null;
        }
        return new ActivitySubscriptionResponse(
            activitySubscription.getId(),
            UserResponse.from(activitySubscription.getUser()),
            ActivityInfoResponse.from((cn.edu.zju.cs.jobmate.models.ActivityInfo) activitySubscription.getInfo()),
            activitySubscription.getSubscribedAt()
        );
    }
}