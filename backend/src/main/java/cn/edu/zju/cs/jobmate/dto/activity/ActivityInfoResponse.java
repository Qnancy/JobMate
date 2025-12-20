package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ActivityInfo response DTO.
 */
@Data
@Builder
public class ActivityInfoResponse {

    private Integer id;
    private CompanyResponse company;
    private String title;
    private LocalDateTime time;
    private String link;
    private String location;
    private String extra;

    /**
     * Convert from ActivityInfo entity to ActivityInfoResponse.
     */
    public static ActivityInfoResponse from(ActivityInfo activityInfo) {
        if (activityInfo == null) {
            return null;
        }
        return new ActivityInfoResponse(
            activityInfo.getId(),
            CompanyResponse.from(activityInfo.getCompany()),
            activityInfo.getTitle(),
            activityInfo.getTime(),
            activityInfo.getLink(),
            activityInfo.getLocation(),
            activityInfo.getExtra()
        );
    }
}
