package cn.edu.zju.cs.jobmate.dto.favorite;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Query DTO for paginated favorite listing.
 *
 * <p>When {@code targetType} is null, returns all of the user's favorites (jobs and activities
 * mixed); otherwise filter by the given target type.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FavoriteListRequest extends PageRequest {

    /** Optional; null means "return both JOB and ACTIVITY favorites". */
    private FavoriteTargetType targetType;
}
