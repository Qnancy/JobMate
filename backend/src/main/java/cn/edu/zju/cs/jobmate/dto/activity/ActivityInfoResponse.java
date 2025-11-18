package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Activity information response DTO
 */
@Data
@Builder
public class ActivityInfoResponse {
    
    private Integer id;
    private CompanyResponse company;
    private String title;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    
    private String link;
    private String city;
    private String location;
    private String extra;

    /**
     * Convert from ActivityInfo entity to ActivityInfoResponse
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
            activityInfo.getCity(),
            activityInfo.getLocation(),
            activityInfo.getExtra()
        );
    }
}
