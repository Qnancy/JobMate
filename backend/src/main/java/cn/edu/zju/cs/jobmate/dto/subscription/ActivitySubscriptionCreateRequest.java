package cn.edu.zju.cs.jobmate.dto.subscription;

import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoResponse;
import cn.edu.zju.cs.jobmate.dto.user.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Activity subscription creation request DTO
 */
@Data
@Builder
public class ActivitySubscriptionCreateRequest {
    
    @Valid
    @NotNull(message = "User information cannot be empty")
    private UserResponse user;
    
    @Valid
    @NotNull(message = "Activity information cannot be empty")
    private ActivityInfoResponse activityInfo;
}