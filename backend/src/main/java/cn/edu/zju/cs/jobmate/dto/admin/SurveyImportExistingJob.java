package cn.edu.zju.cs.jobmate.dto.admin;

import cn.edu.zju.cs.jobmate.enums.EducationRequirement;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import lombok.Builder;
import lombok.Data;

/**
 * Snapshot of an existing job shown when import would overwrite data.
 */
@Data
@Builder
public class SurveyImportExistingJob {

    private Long id;
    private String position;
    private RecruitType recruitType;
    private String recruitTypeLabel;
    private String location;
    private String link;
    private String extra;
    private EducationRequirement educationRequirement;
    private String educationLabel;
    private String deadline;

    public static SurveyImportExistingJob from(JobInfo job) {
        if (job == null) {
            return null;
        }
        RecruitType recruitType = job.getRecruitType();
        EducationRequirement edu = job.getEducationRequirement();
        return SurveyImportExistingJob.builder()
            .id(job.getId())
            .position(job.getPosition())
            .recruitType(recruitType)
            .recruitTypeLabel(recruitType != null ? recruitTypeLabel(recruitType) : null)
            .location(job.getLocation())
            .link(job.getLink())
            .extra(job.getExtra())
            .educationRequirement(edu)
            .educationLabel(edu != null ? edu.getLabelZh() : null)
            .deadline(job.getDeadline() != null ? job.getDeadline().toString() : null)
            .build();
    }

    private static String recruitTypeLabel(RecruitType type) {
        return switch (type) {
            case INTERN -> "实习";
            case CAMPUS -> "校招";
            case EXPERIENCED -> "社招";
        };
    }
}
