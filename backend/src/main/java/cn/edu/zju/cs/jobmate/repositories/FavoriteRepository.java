package cn.edu.zju.cs.jobmate.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import cn.edu.zju.cs.jobmate.models.Favorite;

/**
 * DAO for {@link Favorite}.
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUser_IdAndTargetTypeAndTargetId(
        Long userId, FavoriteTargetType targetType, Long targetId);

    boolean existsByUser_IdAndTargetTypeAndTargetId(
        Long userId, FavoriteTargetType targetType, Long targetId);

    Page<Favorite> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Favorite> findByUser_IdAndTargetTypeOrderByCreatedAtDesc(
        Long userId, FavoriteTargetType targetType, Pageable pageable);

    /**
     * Batch check: given a list of target ids, return the subset that the user has already favorited.
     */
    @Query("select f.targetId from Favorite f " +
        "where f.user.id = :userId and f.targetType = :targetType and f.targetId in :ids")
    List<Long> findFavoritedTargetIds(
        @Param("userId") Long userId,
        @Param("targetType") FavoriteTargetType targetType,
        @Param("ids") Collection<Long> ids
    );

    @Modifying
    @Query("delete from Favorite f " +
        "where f.user.id = :userId and f.targetType = :targetType and f.targetId = :targetId")
    int deleteByUserAndTarget(
        @Param("userId") Long userId,
        @Param("targetType") FavoriteTargetType targetType,
        @Param("targetId") Long targetId
    );
}
