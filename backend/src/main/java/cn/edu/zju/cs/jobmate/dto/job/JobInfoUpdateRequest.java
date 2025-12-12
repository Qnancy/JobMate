package cn.edu.zju.cs.jobmate.dto.job;

import com.fasterxml.jackson.annotation.JsonProperty;

import cn.edu.zju.cs.jobmate.dto.common.UpdateRequest;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * JobInfo update request DTO.
 */
@Data
@Builder
public class JobInfoUpdateRequest implements UpdateRequest<JobInfo> {

    @JsonProperty("company_id")
    private Integer companyId;

    @JsonProperty("recruit_type")
    private RecruitType recruitType;

    @Size(max = 128, message = "Position length cannot exceed 128")
    private String position;

    private String link;
    private String city;
    private String location;
    private String extra;

    @Override
    public boolean isUpdatable() {
        return companyId != null ||
            recruitType != null ||
            position != null ||
            link != null ||
            city != null ||
            location != null ||
            extra != null;
    }

    @Override
    public void apply(JobInfo jobInfo) {
        // Update Company field in Service layer.
        jobInfo.setRecruitType(recruitType);
        jobInfo.setPosition(position);
        jobInfo.setLink(link);
        jobInfo.setCity(city);
        jobInfo.setLocation(location);
        jobInfo.setExtra(extra);
    }
}
