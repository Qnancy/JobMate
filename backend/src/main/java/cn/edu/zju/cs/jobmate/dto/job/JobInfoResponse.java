package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.enums.EducationRequirement;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * JobInf response DTO.
 */
@Data
@Builder
public class JobInfoResponse {

    private Long id;
    private CompanyResponse company;
    private RecruitType recruitType;
    private String position;
    private String link;
    private String location;
    private String extra;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deadline;

    private EducationRequirement educationRequirement;

    /**
     * Convert from JobInfo entity to JobInfoResponse.
     */
    public static JobInfoResponse from(JobInfo jobInfo) {
        if (jobInfo == null) {
            return null;
        }
        return JobInfoResponse.builder()
            .id(jobInfo.getId())
            .company(CompanyResponse.from(jobInfo.getCompany()))
            .recruitType(jobInfo.getRecruitType())
            .position(jobInfo.getPosition())
            .link(jobInfo.getLink())
            .location(jobInfo.getLocation())
            .extra(jobInfo.getExtra())
            .createdAt(jobInfo.getCreatedAt())
            .updatedAt(jobInfo.getUpdatedAt())
            .deadline(jobInfo.getDeadline())
            .educationRequirement(jobInfo.getEducationRequirement())
            .build();
    }
}
