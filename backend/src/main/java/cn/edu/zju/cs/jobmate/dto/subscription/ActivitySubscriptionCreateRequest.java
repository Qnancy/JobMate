package cn.edu.zju.cs.jobmate.dto.subscription;

import cn.edu.zju.cs.jobmate.dto.common.CreateRequest;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * ActivitySubscription creation request DTO.
 */
@Data
@Builder
public class ActivitySubscriptionCreateRequest implements CreateRequest<ActivitySubscription> {

    @NotNull(message = "User id cannot be null")
    private Long userId;

    @NotNull(message = "Activity information id cannot be null")
    private Long activityInfoId;

    @Override
    public ActivitySubscription toModel() {
        return new ActivitySubscription();
    }

    @Override
    public String toString() {
        return "ActivitySubscriptionCreateRequest{" +
                "userId=" + userId +
                ", activityInfoId=" + activityInfoId +
                '}';
    }
}
