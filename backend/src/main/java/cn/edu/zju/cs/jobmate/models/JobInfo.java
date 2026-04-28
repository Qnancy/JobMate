package cn.edu.zju.cs.jobmate.models;

import cn.edu.zju.cs.jobmate.enums.EducationRequirement;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.bases.Info;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * JobInfo entity.
 * 
 * @see Info
 */
@Entity
@Table(name = "job_infos")
@EntityListeners(AuditingEntityListener.class)
public class JobInfo extends Info {

    @Enumerated(EnumType.STRING)
    @Column(name = "recruit_type", nullable = false)
    private RecruitType recruitType;

    @Column(name = "position", nullable = false, length = 128)
    private String position;

    /**
     * Auto-populated on insert by {@link AuditingEntityListener}.
     * The DEFAULT clause covers existing rows during ddl-auto schema migration.
     */
    @CreatedDate
    @Column(
        name = "created_at",
        nullable = false,
        updatable = false,
        columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime createdAt;

    /**
     * Auto-populated on insert and refreshed on every update.
     */
    @LastModifiedDate
    @Column(
        name = "updated_at",
        nullable = false,
        columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime updatedAt;

    /**
     * Optional application deadline set by the admin.
     */
    @Column(name = "deadline", columnDefinition = "TIMESTAMP NULL")
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "education_requirement",
        nullable = false,
        length = 32,
        columnDefinition = "VARCHAR(32) NOT NULL DEFAULT 'UNSPECIFIED'"
    )
    private EducationRequirement educationRequirement = EducationRequirement.UNSPECIFIED;

    protected JobInfo() {
    }

    public JobInfo(
        RecruitType recruitType,
        String position,
        String link,
        String location,
        String extra
    ) {
        this(recruitType, position, null, link, location, extra, EducationRequirement.UNSPECIFIED);
    }

    public JobInfo(
        RecruitType recruitType,
        String position,
        LocalDateTime deadline,
        String link,
        String location,
        String extra
    ) {
        this(recruitType, position, deadline, link, location, extra, EducationRequirement.UNSPECIFIED);
    }

    public JobInfo(
        RecruitType recruitType,
        String position,
        LocalDateTime deadline,
        String link,
        String location,
        String extra,
        EducationRequirement educationRequirement
    ) {
        this.recruitType = recruitType;
        this.position = position;
        this.deadline = deadline;
        setLink(link);
        setLocation(location);
        setExtra(extra);
        this.educationRequirement = educationRequirement != null
            ? educationRequirement
            : EducationRequirement.UNSPECIFIED;
    }

    public RecruitType getRecruitType() {
        return recruitType;
    }

    public String getPosition() {
        return position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setRecruitType(RecruitType recruitType) {
        this.recruitType = recruitType;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public EducationRequirement getEducationRequirement() {
        return educationRequirement;
    }

    public void setEducationRequirement(EducationRequirement educationRequirement) {
        this.educationRequirement = educationRequirement != null
            ? educationRequirement
            : EducationRequirement.UNSPECIFIED;
    }

    @Override
    public String toString() {
        return "JobInfo {" +
                "id=" + getId() +
                ", company=" + getCompany() +
                ", recruitType=" + recruitType +
                ", position=" + ToStringUtil.wrap(position) +
                ", location=" + ToStringUtil.wrap(getLocation()) +
                ", link=" + ToStringUtil.wrap(getLink()) +
                ", extra=" + ToStringUtil.wrap(getExtra()) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deadline=" + deadline +
                ", educationRequirement=" + educationRequirement +
                '}';
    }
}
