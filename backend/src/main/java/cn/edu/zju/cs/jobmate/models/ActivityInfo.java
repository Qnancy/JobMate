package cn.edu.zju.cs.jobmate.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * ActivityInfo entity.
 * 
 * @see Info
 */
@Entity
@Table(name = "activity_info")
public class ActivityInfo extends Info {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    protected ActivityInfo() {
    }

    public ActivityInfo(Company company, String title, LocalDateTime time, String location) {
        setCompany(company);
        setLocation(location);
        this.title = title;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ActivityInfo {" +
                "id=" + getId() +
                ", company=" + getCompany() +
                ", title='" + title + '\'' +
                ", time=" + time +
                ", location='" + getLocation() + '\'' +
                ", link='" + getLink() + '\'' +
                ", extra='" + getExtra() + '\'' +
                '}';
    }
}
