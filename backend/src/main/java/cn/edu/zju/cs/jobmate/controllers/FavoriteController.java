package cn.edu.zju.cs.jobmate.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteCheckBatchRequest;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteCheckBatchResponse;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteListRequest;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteRequest;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteResponse;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteToggleResponse;
import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import cn.edu.zju.cs.jobmate.services.FavoriteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Favorite REST Controller.
 *
 * <p>All endpoints operate on the currently authenticated user (identified via JWT).
 * Paths:
 * <ul>
 *   <li>POST /api/favorites — add a favorite (idempotent)</li>
 *   <li>DELETE /api/favorites — remove a favorite (idempotent)</li>
 *   <li>POST /api/favorites/toggle — flip favorited state</li>
 *   <li>GET /api/favorites/check — check a single target</li>
 *   <li>POST /api/favorites/check-batch — check many targets in one round-trip</li>
 *   <li>GET /api/favorites — paginated list (optionally filtered by target type)</li>
 * </ul>
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Add the target to the current user's favorites. Idempotent.
     *
     * @apiNote POST /api/favorites
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FavoriteToggleResponse>> add(
        @Valid @RequestBody FavoriteRequest request
    ) {
        boolean created = favoriteService.add(request.getTargetType(), request.getTargetId());
        FavoriteToggleResponse data = FavoriteToggleResponse.builder()
            .targetType(request.getTargetType())
            .targetId(request.getTargetId())
            .favorited(true)
            .build();
        String msg = created ? "收藏成功" : "已经在收藏夹中";
        return ResponseEntity.ok(ApiResponse.ok(msg, data));
    }

    /**
     * Remove the target from the current user's favorites. Idempotent.
     *
     * @apiNote DELETE /api/favorites
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<FavoriteToggleResponse>> remove(
        @Valid @RequestBody FavoriteRequest request
    ) {
        boolean deleted = favoriteService.remove(request.getTargetType(), request.getTargetId());
        FavoriteToggleResponse data = FavoriteToggleResponse.builder()
            .targetType(request.getTargetType())
            .targetId(request.getTargetId())
            .favorited(false)
            .build();
        String msg = deleted ? "已取消收藏" : "原本就不在收藏夹中";
        return ResponseEntity.ok(ApiResponse.ok(msg, data));
    }

    /**
     * Flip the favorited state of the target.
     *
     * @apiNote POST /api/favorites/toggle
     */
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<FavoriteToggleResponse>> toggle(
        @Valid @RequestBody FavoriteRequest request
    ) {
        boolean nowFavorited = favoriteService.toggle(request.getTargetType(), request.getTargetId());
        FavoriteToggleResponse data = FavoriteToggleResponse.builder()
            .targetType(request.getTargetType())
            .targetId(request.getTargetId())
            .favorited(nowFavorited)
            .build();
        return ResponseEntity.ok(ApiResponse.ok(nowFavorited ? "已收藏" : "已取消收藏", data));
    }

    /**
     * Check whether the current user has this single target favorited.
     *
     * @apiNote GET /api/favorites/check?target_type=JOB&target_id=1
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<FavoriteToggleResponse>> check(
        @NotNull @RequestParam("target_type") FavoriteTargetType targetType,
        @NotNull @Positive @RequestParam("target_id") Long targetId
    ) {
        boolean favorited = favoriteService.exists(targetType, targetId);
        FavoriteToggleResponse data = FavoriteToggleResponse.builder()
            .targetType(targetType)
            .targetId(targetId)
            .favorited(favorited)
            .build();
        return ResponseEntity.ok(ApiResponse.ok("查询成功", data));
    }

    /**
     * Batch-check: given a list of target ids, return the subset the current user has favorited.
     *
     * @apiNote POST /api/favorites/check-batch
     */
    @PostMapping("/check-batch")
    public ResponseEntity<ApiResponse<FavoriteCheckBatchResponse>> checkBatch(
        @Valid @RequestBody FavoriteCheckBatchRequest request
    ) {
        List<Long> favoritedIds = favoriteService.existsBatch(request.getTargetType(), request.getIds());
        FavoriteCheckBatchResponse data = FavoriteCheckBatchResponse.builder()
            .targetType(request.getTargetType())
            .favoritedIds(favoritedIds)
            .build();
        return ResponseEntity.ok(ApiResponse.ok("查询成功", data));
    }

    /**
     * Paginated list of the current user's favorites, newest-first.
     *
     * @apiNote GET /api/favorites?target_type=JOB&page=1&page_size=10
     *         target_type is optional; when omitted, returns both JOB and ACTIVITY favorites.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FavoriteResponse>>> list(
        @Valid FavoriteListRequest request
    ) {
        Page<FavoriteResponse> page = favoriteService.list(request);
        return ResponseEntity.ok(ApiResponse.ok("获取成功", PageResponse.from(page)));
    }
}
