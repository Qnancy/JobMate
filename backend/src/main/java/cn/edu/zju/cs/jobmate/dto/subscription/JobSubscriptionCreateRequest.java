package cn.edu.zju.cs.jobmate.dto.subscription;

import cn.edu.zju.cs.jobmate.dto.common.CreateRequest;
import cn.edu.zju.cs.jobmate.models.JobSubscription;

import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

/**
 * JobSubscription creation request DTO.
 */
@Data
@Builder
public class JobSubscriptionCreateRequest implements CreateRequest<JobSubscription> {

    @NotNull(message = "User id cannot be null")
    private Long userId;

    @NotNull(message = "Job information id cannot be null")
    private Long jobInfoId;

    @Override
    public JobSubscription toModel() {
        return new JobSubscription();
    }

    @Override
    public String toString() {
        return "JobSubscriptionCreateRequest{" +
                "userId=" + userId +
                ", jobInfoId=" + jobInfoId +
                '}';
    }
}
