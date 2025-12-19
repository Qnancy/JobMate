package cn.edu.zju.cs.jobmate.dto.subscription;

import cn.edu.zju.cs.jobmate.dto.job.JobInfoResponse;
import cn.edu.zju.cs.jobmate.dto.user.UserResponse;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Job subscription response DTO
 */
@Data
@Builder
public class JobSubscriptionResponse {
    
    private Integer id;
    private UserResponse user;
    private JobInfoResponse jobInfo;
    
    private LocalDateTime subscribedAt;

    /**
     * Convert from JobSubscription entity to JobSubscriptionResponse
     */
    public static JobSubscriptionResponse from(JobSubscription jobSubscription) {
        if (jobSubscription == null) {
            return null;
        }
        return new JobSubscriptionResponse(
            jobSubscription.getId(),
            UserResponse.from(jobSubscription.getUser()),
            JobInfoResponse.from((cn.edu.zju.cs.jobmate.models.JobInfo) jobSubscription.getInfo()),
            jobSubscription.getSubscribedAt()
        );
    }
}