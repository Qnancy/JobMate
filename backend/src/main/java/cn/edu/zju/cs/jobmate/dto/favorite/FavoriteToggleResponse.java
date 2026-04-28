package cn.edu.zju.cs.jobmate.dto.favorite;

import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Response for toggle / check single-item favorite APIs.
 *
 * <p>{@code favorited} reflects the state AFTER the request finishes (true = user now has this
 * item in favorites; false = user does not).
 */
@Data
@Builder
@AllArgsConstructor
public class FavoriteToggleResponse {

    private FavoriteTargetType targetType;
    private Long targetId;
    private boolean favorited;
}
