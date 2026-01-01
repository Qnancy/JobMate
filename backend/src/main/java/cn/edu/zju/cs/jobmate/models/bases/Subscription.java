package cn.edu.zju.cs.jobmate.models.bases;

import cn.edu.zju.cs.jobmate.models.User;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

/**
 * Subscription class is the base class for JobSubscription and ActivitySubscription entity.
 */
@MappedSuperclass
public abstract class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "subscribed_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime subscribedAt;

    @PrePersist
    protected void onCreate() {
        this.subscribedAt = LocalDateTime.now();
    }

    protected Subscription() {
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }

    public abstract Info getInfo();

    public void setUser(User user) {
        this.user = user;
    }
}
