package cn.edu.zju.cs.jobmate.models;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "subscription",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_activity", 
                            columnNames = {"user_id", "activity_info_id"}),
           @UniqueConstraint(name = "uk_user_job", 
                            columnNames = {"user_id", "job_info_id"})
       })
public class Subsription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_info_id", updatable = false)
    private ActivityInfo activityInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_info_id", updatable = false)
    private JobInfo jobInfo;

    @Column(name = "subscribed_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime subscribedAt;

    protected Subsription() {
    }

    public Subsription(User user, ActivityInfo activityInfo) {
        Assert.notNull(activityInfo, "Subscribed info must not be null");
        this.user = user;
        this.activityInfo = activityInfo;
    }

    public Subsription(User user, JobInfo jobInfo) {
        Assert.notNull(jobInfo, "Subscribed info must not be null.");
        this.user = user;
        this.jobInfo = jobInfo;
    }

    @PrePersist
    protected void onCreate() {
        this.subscribedAt = LocalDateTime.now();

        // Validate info entity.
        // TODO: Print error log.
        if (this.activityInfo == null && this.jobInfo == null) {
            throw new IllegalStateException("Empty or null subscription info.");
        }
        if (this.activityInfo != null && this.jobInfo != null) {
            throw new IllegalStateException("Conflicting subscription info.");
        }
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
        if (this.activityInfo != null) {
            return this.activityInfo;
        }
        if (this.jobInfo != null) {
            return this.jobInfo;
        }
        return null;
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
                ", subscribedAt=" + subscribedAt +
                '}';
    }
}
