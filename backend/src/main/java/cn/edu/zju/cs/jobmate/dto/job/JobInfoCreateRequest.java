package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.enums.RecruitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 职位信息创建请求DTO
 */
public class JobInfoCreateRequest {
    
    @NotNull(message = "公司ID不能为空")
    private Integer companyId;
    
    @NotNull(message = "招聘类型不能为空")
    private RecruitType recruitType;
    
    @NotBlank(message = "职位名称不能为空")
    @Size(max = 128, message = "职位名称长度不能超过128")
    private String position;
    
    private String link;
    private String city;
    private String location;
    private String extra;

    public JobInfoCreateRequest() {
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

