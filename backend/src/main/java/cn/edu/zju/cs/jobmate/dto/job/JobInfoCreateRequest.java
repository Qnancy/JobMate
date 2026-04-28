package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.common.CreateRequest;
import cn.edu.zju.cs.jobmate.enums.EducationRequirement;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * JobInfo creation request DTO.
 */
@Data
@Builder
public class JobInfoCreateRequest implements CreateRequest<JobInfo> {

    @NotNull(message = "Company id cannot be null")
    private Long companyId;

    @NotNull(message = "Recruitment type cannot be null")
    private RecruitType recruitType;

    @NotBlank(message = "Position cannot be empty")
    @Size(max = 128, message = "Position length cannot exceed 128")
    private String position;

    private String link;
    private String location;
    private String extra;

    /**
     * Optional application deadline. Created/updated timestamps are auto-managed.
     */
    private LocalDateTime deadline;

    /** 学历要求；缺省为 {@link EducationRequirement#UNSPECIFIED}。 */
    private EducationRequirement educationRequirement;

    @Override
    public JobInfo toModel() {
        EducationRequirement edu = educationRequirement != null
            ? educationRequirement
            : EducationRequirement.UNSPECIFIED;
        return new JobInfo(recruitType, position, deadline, link, location, extra, edu);
    }

    @Override
    public String toString() {
        return "JobInfoCreateRequest{" +
                "companyId=" + companyId +
                ", recruitType=" + recruitType +
                ", position=" + ToStringUtil.wrap(position) +
                ", link=" + ToStringUtil.wrap(link) +
                ", location=" + ToStringUtil.wrap(location) +
                ", extra=" + ToStringUtil.wrap(extra) +
                ", deadline=" + deadline +
                ", educationRequirement=" + educationRequirement +
                '}';
    }
}
