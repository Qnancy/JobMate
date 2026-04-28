package cn.edu.zju.cs.jobmate.dto.favorite;

import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shared request DTO for add / remove / toggle / check single-item favorite APIs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest {

    @NotNull(message = "目标类型不能为空")
    private FavoriteTargetType targetType;

    @NotNull(message = "目标 ID 不能为空")
    @Positive(message = "目标 ID 必须为正整数")
    private Long targetId;
}
