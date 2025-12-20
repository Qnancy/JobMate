package cn.edu.zju.cs.jobmate.dto.subscription;

import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoResponse;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ActivitySubscription response DTO.
 */
@Data
@Builder
public class ActivitySubscriptionResponse {

    private Integer id;
    private ActivityInfoResponse activityInfo;
    private LocalDateTime subscribedAt;

    /**
     * Convert from ActivitySubscription entity to ActivitySubscriptionResponse.
     */
    public static ActivitySubscriptionResponse from(ActivitySubscription activitySubscription) {
        if (activitySubscription == null) {
            return null;
        }
        return new ActivitySubscriptionResponse(
            activitySubscription.getId(),
            ActivityInfoResponse.from(activitySubscription.getInfo()),
            activitySubscription.getSubscribedAt()
        );
    }
}
