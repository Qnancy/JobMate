package cn.edu.zju.cs.jobmate.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

/**
 * Info class is the base class for JobInfo and ActivityInfo entity.
 */
@Entity
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "link")
    private String link;

    @Column(name = "location")
    private String location;

    @Column(name = "extra", columnDefinition = "TEXT")
    private String extra;

    protected Info() {
    }

    public Integer getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getLink() {
        return link;
    }

    public String getLocation() {
        return location;
    }

    public String getExtra() {
        return extra;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
