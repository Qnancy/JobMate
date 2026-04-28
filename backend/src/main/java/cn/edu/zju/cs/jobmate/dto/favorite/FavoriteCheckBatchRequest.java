package cn.edu.zju.cs.jobmate.dto.favorite;

import java.util.List;

import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Batch-check request: "of these target ids, which ones has the current user already favorited?"
 *
 * <p>Useful for rendering a list page where every card needs a ★/☆ icon.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCheckBatchRequest {

    @NotNull(message = "目标类型不能为空")
    private FavoriteTargetType targetType;

    @NotEmpty(message = "目标 ID 列表不能为空")
    @Size(max = 200, message = "单次最多检查 200 个 ID")
    private List<@Positive(message = "目标 ID 必须为正整数") Long> ids;
}
