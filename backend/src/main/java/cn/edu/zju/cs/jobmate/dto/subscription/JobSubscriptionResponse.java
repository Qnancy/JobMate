package cn.edu.zju.cs.jobmate.dto.subscription;

import cn.edu.zju.cs.jobmate.dto.job.JobInfoResponse;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * JobSubscription response DTO.
 */
@Data
@Builder
public class JobSubscriptionResponse {

    private Integer id;
    private JobInfoResponse jobInfo;
    private LocalDateTime subscribedAt;

    /**
     * Convert from JobSubscription entity to JobSubscriptionResponse.
     */
    public static JobSubscriptionResponse from(JobSubscription jobSubscription) {
        if (jobSubscription == null) {
            return null;
        }
        return new JobSubscriptionResponse(
            jobSubscription.getId(),
            JobInfoResponse.from(jobSubscription.getInfo()),
            jobSubscription.getSubscribedAt()
        );
    }
}
