package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import lombok.Builder;
import lombok.Data;

/**
 * JobInf response DTO.
 */
@Data
@Builder
public class JobInfoResponse {

    private Integer id;
    private CompanyResponse company;
    private RecruitType recruitType;
    private String position;
    private String link;
    private String location;
    private String extra;

    /**
     * Convert from JobInfo entity to JobInfoResponse.
     */
    public static JobInfoResponse from(JobInfo jobInfo) {
        if (jobInfo == null) {
            return null;
        }
        return new JobInfoResponse(
            jobInfo.getId(),
            CompanyResponse.from(jobInfo.getCompany()),
            jobInfo.getRecruitType(),
            jobInfo.getPosition(),
            jobInfo.getLink(),
            jobInfo.getLocation(),
            jobInfo.getExtra()
        );
    }
}
