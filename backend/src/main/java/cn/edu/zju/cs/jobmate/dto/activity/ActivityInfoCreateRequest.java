package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.common.CreateRequest;
import cn.edu.zju.cs.jobmate.enums.ActivityType;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ActivityInfo creation request DTO.
 */
@Data
@Builder
public class ActivityInfoCreateRequest implements CreateRequest<ActivityInfo> {

    @NotNull(message = "Company id cannot be null")
    private Long companyId;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotNull(message = "Time cannot be null")
    private LocalDateTime time;

    /**
     * Optional in the request. When omitted we fall back to {@link ActivityType#LECTURE}.
     */
    private ActivityType type;

    private String link;
    private String location;
    private String extra;

    @Override
    public ActivityInfo toModel() {
        ActivityType resolvedType = type != null ? type : ActivityType.LECTURE;
        return new ActivityInfo(title, time, resolvedType, link, location, extra);
    }

    @Override
    public String toString() {
        return "ActivityInfoCreateRequest{" +
                "companyId=" + companyId +
                ", title=" + ToStringUtil.wrap(title) +
                ", time=" + time +
                ", type=" + type +
                ", link=" + ToStringUtil.wrap(link) +
                ", location=" + ToStringUtil.wrap(location) +
                ", extra=" + ToStringUtil.wrap(extra) +
                '}';
    }
}
