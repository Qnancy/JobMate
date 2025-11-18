package cn.edu.zju.cs.jobmate.models.bases;

import cn.edu.zju.cs.jobmate.models.Company;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

/**
 * Info class is the base class for JobInfo and ActivityInfo entity.
 */
@MappedSuperclass
public abstract class Info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "link")
    private String link;

    @Column(name = "city")
    private String city;

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

    public String getCity() {
        return city;
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

    public void setCity(String city) {
        this.city = city;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
