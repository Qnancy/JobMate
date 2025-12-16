package cn.edu.zju.cs.jobmate.models;

import cn.edu.zju.cs.jobmate.models.bases.Subscription;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JobSubscription entity.
 * 
 * @see Subscription
 */
@Entity
@Table(name = "job_subscriptions")
public class JobSubscription extends Subscription {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_info_id", nullable = false)
    private JobInfo jobInfo;

    protected JobSubscription() {
    }

    public JobSubscription(User user, JobInfo jobInfo) {
        setUser(user);
        this.jobInfo = jobInfo;
    }

    @Override
    public JobInfo getInfo() {
        return jobInfo;
    }

    public void setInfo(JobInfo info) {
        this.jobInfo = info;
    }

    @Override
    public String toString() {
        return "JobSubscription{" +
                "id=" + getId() +
                ", user=" + getUser() +
                ", jobInfo=" + jobInfo +
                ", subscribedAt=" + getSubscribedAt() +
                '}';
    }
}
