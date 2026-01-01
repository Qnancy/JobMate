package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.common.UpdateRequest;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * JobInfo update request DTO.
 */
@Data
@Builder
public class JobInfoUpdateRequest implements UpdateRequest<JobInfo> {

    private Long companyId;
    private RecruitType recruitType;

    @Size(max = 128, message = "Position length cannot exceed 128")
    private String position;

    private String link;
    private String location;
    private String extra;

    @Override
    public boolean isUpdatable() {
        return companyId != null ||
            recruitType != null ||
            position != null ||
            link != null ||
            location != null ||
            extra != null;
    }

    @Override
    public void apply(JobInfo jobInfo) {
        // Update Company field in Service layer.
        if (recruitType != null) { jobInfo.setRecruitType(recruitType); }
        if (position != null) { jobInfo.setPosition(position); }
        if (link != null) { jobInfo.setLink(link); }
        if (location != null) { jobInfo.setLocation(location); }
        if (extra != null) { jobInfo.setExtra(extra); }
    }

    @Override
    public String toString() {
        return "JobInfoUpdateRequest{" +
            "companyId=" + companyId +
            ", recruitType=" + recruitType +
            ", position=" + ToStringUtil.wrap(position) +
            ", link=" + ToStringUtil.wrap(link) +
            ", location=" + ToStringUtil.wrap(location) +
            ", extra=" + ToStringUtil.wrap(extra) +
            '}';
    }
}
