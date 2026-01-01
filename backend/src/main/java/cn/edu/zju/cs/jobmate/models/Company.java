package cn.edu.zju.cs.jobmate.models;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.bases.BaseEntity;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Company entity.
 */
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CompanyType type;

    protected Company() {
    }

    public Company(String name, CompanyType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public CompanyType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CompanyType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + getId() +
                ", name=" + ToStringUtil.wrap(name) +
                ", type=" + type +
                '}';
    }
}
