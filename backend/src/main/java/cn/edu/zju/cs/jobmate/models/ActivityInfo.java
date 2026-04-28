package cn.edu.zju.cs.jobmate.models;

import cn.edu.zju.cs.jobmate.enums.ActivityType;
import cn.edu.zju.cs.jobmate.models.bases.Info;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * ActivityInfo entity.
 * 
 * @see Info
 */
@Entity
@Table(name = "activity_infos")
public class ActivityInfo extends Info {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    /**
     * Activity type. The DEFAULT clause guarantees existing rows get a sane value
     * when Hibernate's `ddl-auto: update` adds the column on schema migration.
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "type",
        nullable = false,
        columnDefinition = "VARCHAR(32) NOT NULL DEFAULT 'LECTURE'"
    )
    private ActivityType type = ActivityType.LECTURE;

    protected ActivityInfo() {
    }

    public ActivityInfo(
        String title,
        LocalDateTime time,
        String link,
        String location,
        String extra
    ) {
        this(title, time, ActivityType.LECTURE, link, location, extra);
    }

    public ActivityInfo(
        String title,
        LocalDateTime time,
        ActivityType type,
        String link,
        String location,
        String extra
    ) {
        this.title = title;
        this.time = time;
        this.type = type != null ? type : ActivityType.LECTURE;
        setLink(link);
        setLocation(location);
        setExtra(extra);
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public ActivityType getType() {
        return type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setType(ActivityType type) {
        this.type = type != null ? type : ActivityType.LECTURE;
    }

    @Override
    public String toString() {
        return "ActivityInfo {" +
                "id=" + getId() +
                ", company=" + getCompany() +
                ", title=" + ToStringUtil.wrap(title) +
                ", time=" + time +
                ", type=" + type +
                ", location=" + ToStringUtil.wrap(getLocation()) +
                ", link=" + ToStringUtil.wrap(getLink()) +
                ", extra=" + ToStringUtil.wrap(getExtra()) +
                '}';
    }
}
