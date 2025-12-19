package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.common.CreateRequest;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * ActivityInfo creation request DTO.
 */
@Data
@Builder
public class ActivityInfoCreateRequest implements CreateRequest<ActivityInfo> {

    @JsonProperty("company_id")
    @NotNull(message = "Company id cannot be null")
    private Integer companyId;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotNull(message = "Time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String link;
    private String location;
    private String extra;

    @Override
    public ActivityInfo toModel() {
        return new ActivityInfo(title, time, link, location, extra);
    }

    @Override
    public String toString() {
        return "ActivityInfoCreateRequest{" +
                "companyId=" + companyId +
                ", title=" + ToStringUtil.wrap(title) +
                ", time=" + time +
                ", link=" + ToStringUtil.wrap(link) +
                ", location=" + ToStringUtil.wrap(location) +
                ", extra=" + ToStringUtil.wrap(extra) +
                '}';
    }
}
