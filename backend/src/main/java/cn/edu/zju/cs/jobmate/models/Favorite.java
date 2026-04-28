package cn.edu.zju.cs.jobmate.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import cn.edu.zju.cs.jobmate.models.bases.BaseEntity;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;

/**
 * Favorite entity.
 *
 * <p>A favorite is a unidirectional "the user bookmarked this thing" relation.
 * One row per (user, target_type, target_id). Keeps the model flat so more
 * target types (e.g. COMPANY) can be added later without changing the schema.
 */
@Entity
@Table(
    name = "favorites",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_favorite_user_target",
            columnNames = {"user_id", "target_type", "target_id"}
        )
    },
    indexes = {
        @Index(name = "idx_favorite_user_type", columnList = "user_id,target_type")
    }
)
public class Favorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 16)
    private FavoriteTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "created_at", updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    protected Favorite() {
    }

    public Favorite(User user, FavoriteTargetType targetType, Long targetId) {
        this.user = user;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public FavoriteTargetType getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTargetType(FavoriteTargetType targetType) {
        this.targetType = targetType;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + getId() +
                ", user=" + ToStringUtil.wrap(user != null ? user.getUsername() : null) +
                ", targetType=" + targetType +
                ", targetId=" + targetId +
                ", createdAt=" + createdAt +
                '}';
    }
}
