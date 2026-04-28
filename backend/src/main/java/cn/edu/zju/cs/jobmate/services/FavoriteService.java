package cn.edu.zju.cs.jobmate.services;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;

import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteListRequest;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteResponse;
import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;

/**
 * Favorite service interface. All methods operate on the currently authenticated user.
 *
 * <p>Semantics:
 * <ul>
 *   <li>{@link #add} / {@link #remove} are idempotent — repeated calls return the same final state.</li>
 *   <li>{@link #toggle} flips the current state.</li>
 *   <li>{@link #exists} / {@link #existsBatch} are read-only checks.</li>
 *   <li>{@link #list} paginates the current user's favorites, populating job / activity payloads.</li>
 * </ul>
 */
public interface FavoriteService {

    /**
     * Add (user, target) to favorites if not already present.
     *
     * @return {@code true} iff the row was newly created (i.e. the user didn't already have this favorite).
     * @throws cn.edu.zju.cs.jobmate.exceptions.BusinessException if the target job / activity does not exist.
     */
    boolean add(FavoriteTargetType targetType, Long targetId);

    /**
     * Remove (user, target) from favorites if present.
     *
     * @return {@code true} iff a row was actually deleted.
     */
    boolean remove(FavoriteTargetType targetType, Long targetId);

    /**
     * Flip the favorited state.
     *
     * @return the new favorited state after the flip.
     */
    boolean toggle(FavoriteTargetType targetType, Long targetId);

    /** Whether the current user has this target favorited. */
    boolean exists(FavoriteTargetType targetType, Long targetId);

    /**
     * Batch-check: of the given ids, which ones the current user has favorited.
     * Returns a subset of {@code ids} (order not guaranteed).
     */
    List<Long> existsBatch(FavoriteTargetType targetType, Collection<Long> ids);

    /**
     * List the current user's favorites, newest-first, with job/activity payload enriched.
     *
     * <p>When {@link FavoriteListRequest#getTargetType()} is null, both JOB and ACTIVITY are returned.
     */
    Page<FavoriteResponse> list(FavoriteListRequest request);
}
