package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Job information query request DTO
 * Supports optional parameter queries
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobInfoQueryRequest extends PageRequest {
    
    /**
     * Keyword search (can search job position name, company name, etc.)
     */
    private String keyword;
    
    /**
     * Company type
     */
    private CompanyType companyType;
    
    /**
     * Location
     */
    private String location;
    
    /**
     * Recruitment type
     */
    private RecruitType recruitType;
    
    /**
     * Job position name
     */
    private String position;

    public JobInfoQueryRequest() {
        super();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public void setCompanyType(CompanyType companyType) {
        this.companyType = companyType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}

