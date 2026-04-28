package cn.edu.zju.cs.jobmate.dto.favorite;

import java.time.LocalDateTime;

import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoResponse;
import cn.edu.zju.cs.jobmate.dto.job.JobInfoResponse;
import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import cn.edu.zju.cs.jobmate.models.Favorite;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Response DTO for a single favorite entry.
 *
 * <p>Only one of {@code job} / {@code activity} is populated depending on {@link #targetType}.
 * The other is omitted from JSON (see {@link JsonInclude}).
 */
@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteResponse {

    private Long id;
    private FavoriteTargetType targetType;
    private Long targetId;
    private LocalDateTime createdAt;

    /** Populated iff {@link #targetType} is {@link FavoriteTargetType#JOB}. */
    private JobInfoResponse job;

    /** Populated iff {@link #targetType} is {@link FavoriteTargetType#ACTIVITY}. */
    private ActivityInfoResponse activity;

    /**
     * Base conversion — does not populate the job/activity payload. Use
     * {@link #withJob} / {@link #withActivity} to enrich.
     */
    public static FavoriteResponse from(Favorite favorite) {
        if (favorite == null) {
            return null;
        }
        return FavoriteResponse.builder()
            .id(favorite.getId())
            .targetType(favorite.getTargetType())
            .targetId(favorite.getTargetId())
            .createdAt(favorite.getCreatedAt())
            .build();
    }

    public FavoriteResponse withJob(JobInfoResponse job) {
        this.job = job;
        return this;
    }

    public FavoriteResponse withActivity(ActivityInfoResponse activity) {
        this.activity = activity;
        return this;
    }
}
