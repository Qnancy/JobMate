package cn.edu.zju.cs.jobmate.models;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscription")
public class Subsription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "info_id", updatable = false)
    private ActivityInfo activityInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "info_id", updatable = false)
    private JobInfo jobInfo;

    private enum SubscriptionType {
        ACTIVITY,
        JOB
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SubscriptionType type;

    @Column(name = "subscribed_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime subscribedAt;

    protected Subsription() {
    }

    public Subsription(User user, ActivityInfo activityInfo) {
        Assert.notNull(activityInfo, "Subscribed info must not be null");
        this.user = user;
        this.activityInfo = activityInfo;
        this.type = SubscriptionType.ACTIVITY;
    }

    public Subsription(User user, JobInfo jobInfo) {
        Assert.notNull(jobInfo, "Subscribed info must not be null.");
        this.user = user;
        this.jobInfo = jobInfo;
        this.type = SubscriptionType.JOB;
    }

    @PrePersist
    protected void onCreate() {
        this.subscribedAt = LocalDateTime.now();

        // Validate consistency between type and info.
        if (this.type == SubscriptionType.ACTIVITY && this.activityInfo != null) {
            return ;
        }
        if (this.type == SubscriptionType.JOB && this.jobInfo != null) {
            return ;
        }
        // TODO: Print error log.
        throw new IllegalStateException("Inconsistent subscription type and info.");
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }

    public Info getInfo() {
        return this.type == SubscriptionType.ACTIVITY ? this.activityInfo : this.jobInfo;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", user=" + user +
                ", activityInfo=" + activityInfo +
                ", jobInfo=" + jobInfo +
                ", type=" + type +
                ", subscribedAt=" + subscribedAt +
                '}';
    }
}
