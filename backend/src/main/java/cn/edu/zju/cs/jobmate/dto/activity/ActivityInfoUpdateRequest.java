package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.common.UpdateRequest;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ActivityInfo update request DTO.
 */
@Data
@Builder
public class ActivityInfoUpdateRequest implements UpdateRequest<ActivityInfo> {

    @JsonProperty("company_id")
    private Integer companyId;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String link;
    private String location;
    private String extra;

    @Override
    public boolean isUpdatable() {
        return companyId != null ||
            title != null ||
            time != null ||
            link != null ||
            location != null ||
            extra != null;
    }

    @Override
    public void apply(ActivityInfo activityInfo) {
        // Update Company field in Service layer.
        if (title != null) { activityInfo.setTitle(title); }
        if (time != null) { activityInfo.setTime(time); }
        if (link != null) { activityInfo.setLink(link); }
        if (location != null) { activityInfo.setLocation(location); }
        if (extra != null) { activityInfo.setExtra(extra); }
    }
}
