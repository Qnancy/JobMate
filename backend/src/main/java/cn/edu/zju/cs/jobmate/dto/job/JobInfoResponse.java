package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.JobInfo;

/**
 * 职位信息响应DTO
 */
public class JobInfoResponse {
    
    private Integer id;
    private CompanyResponse company;
    private RecruitType recruitType;
    private String position;
    private String link;
    private String city;
    private String location;
    private String extra;

    public JobInfoResponse() {
    }

    public JobInfoResponse(Integer id, CompanyResponse company, RecruitType recruitType, 
                          String position, String link, String city, String location, String extra) {
        this.id = id;
        this.company = company;
        this.recruitType = recruitType;
        this.position = position;
        this.link = link;
        this.city = city;
        this.location = location;
        this.extra = extra;
    }

    /**
     * 从JobInfo实体转换为JobInfoResponse
     */
    public static JobInfoResponse from(JobInfo jobInfo) {
        if (jobInfo == null) {
            return null;
        }
        return new JobInfoResponse(
            jobInfo.getId(),
            CompanyResponse.from(jobInfo.getCompany()),
            jobInfo.getRecruitType(),
            jobInfo.getPosition(),
            jobInfo.getLink(),
            jobInfo.getCity(),
            jobInfo.getLocation(),
            jobInfo.getExtra()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CompanyResponse getCompany() {
        return company;
    }

    public void setCompany(CompanyResponse company) {
        this.company = company;
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

