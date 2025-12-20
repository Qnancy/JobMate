package cn.edu.zju.cs.jobmate.models;

import cn.edu.zju.cs.jobmate.models.bases.Subscription;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * ActivitySubscription entity.
 * 
 * @see Subscription
 */
@Entity
@Table(name = "activity_subscriptions")
public class ActivitySubscription extends Subscription {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_info_id", nullable = false)
    private ActivityInfo activityInfo;

    public ActivitySubscription() {
    }

    @Override
    public ActivityInfo getInfo() {
        return activityInfo;
    }

    public void setInfo(ActivityInfo activityInfo) {
        this.activityInfo = activityInfo;
    }

    @Override
    public String toString() {
        return "ActivitySubscription{" +
                "id=" + getId() +
                ", user=" + getUser() +
                ", activityInfo=" + activityInfo +
                ", subscribedAt=" + getSubscribedAt() +
                '}';
    }
}
