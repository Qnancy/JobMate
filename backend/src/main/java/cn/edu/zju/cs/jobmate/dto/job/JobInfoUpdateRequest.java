package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.enums.RecruitType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

/**
 * Job information update request DTO
 * All fields are optional
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobInfoUpdateRequest {
    
    private Integer companyId;
    private RecruitType recruitType;
    
    @Size(max = 128, message = "Job position name length cannot exceed 128")
    private String position;
    
    private String link;
    private String city;
    private String location;
    private String extra;

    public JobInfoUpdateRequest() {
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public RecruitType getRecruitType() {
        return recruitType;
    }

    public void setRecruitType(RecruitType recruitType) {
        this.recruitType = recruitType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}

