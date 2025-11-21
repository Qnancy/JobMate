package cn.edu.zju.cs.jobmate.dto.subscription;

import cn.edu.zju.cs.jobmate.dto.job.JobInfoResponse;
import cn.edu.zju.cs.jobmate.dto.user.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Job subscription creation request DTO
 */
@Data
@Builder
public class JobSubscriptionCreateRequest {
    
    @Valid
    @NotNull(message = "User information cannot be empty")
    private UserResponse user;
    
    @Valid
    @NotNull(message = "Job information cannot be empty")
    private JobInfoResponse jobInfo;
}