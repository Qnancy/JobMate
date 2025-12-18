package cn.edu.zju.cs.jobmate.models;

import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.bases.Info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * JobInfo entity.
 * 
 * @see Info
 */
@Entity
@Table(name = "job_infos")
public class JobInfo extends Info {

    @Enumerated(EnumType.STRING)
    @Column(name = "recruit_type", nullable = false)
    private RecruitType recruitType;

    @Column(name = "position", nullable = false, length = 128)
    private String position;

    protected JobInfo() {
    }

    public JobInfo(
        RecruitType recruitType,
        String position,
        String link,
        String city,
        String location,
        String extra
    ) {
        this.recruitType = recruitType;
        this.position = position;
        setLink(link);
        setCity(city);
        setLocation(location);
        setExtra(extra);
    }

    public RecruitType getRecruitType() {
        return recruitType;
    }

    public String getPosition() {
        return position;
    }

    public void setRecruitType(RecruitType recruitType) {
        this.recruitType = recruitType;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "JobInfo {" +
                "id=" + getId() +
                ", company=" + getCompany() +
                ", recruitType=" + recruitType +
                ", position='" + position + "\'" +
                ", city='" + getCity() + '\'' +
                ", location='" + getLocation() + "\'" +
                ", link='" + getLink() + "\'" +
                ", extra='" + getExtra() + "\'" +
                '}';
    }
}
