package cn.edu.zju.cs.jobmate.dto.favorite;

import java.util.List;

import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Response for {@link FavoriteCheckBatchRequest} — the subset of queried ids the current user
 * has favorited.
 */
@Data
@Builder
@AllArgsConstructor
public class FavoriteCheckBatchResponse {

    private FavoriteTargetType targetType;
    private List<Long> favoritedIds;
}
