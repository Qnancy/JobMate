package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.common.UpdateRequest;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ActivityInfo update request DTO.
 */
@Data
@Builder
public class ActivityInfoUpdateRequest implements UpdateRequest<ActivityInfo> {

    private Integer companyId;

    private String title;

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

    @Override
    public String toString() {
        return "ActivityInfoUpdateRequest{" +
            "companyId=" + companyId +
            ", title=" + ToStringUtil.wrap(title) +
            ", time=" + time +
            ", link=" + ToStringUtil.wrap(link) +
            ", location=" + ToStringUtil.wrap(location) +
            ", extra=" + ToStringUtil.wrap(extra) +
            '}';
    }
}
