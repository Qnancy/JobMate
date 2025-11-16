package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 职位信息查询请求DTO
 * 支持可选参数查询
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobInfoQueryRequest extends PageRequest {
    
    /**
     * 关键词搜索（可搜索职位名称、公司名称等）
     */
    private String keyword;
    
    /**
     * 公司类型
     */
    private CompanyType companyType;
    
    /**
     * 地点
     */
    private String location;
    
    /**
     * 招聘类型
     */
    private RecruitType recruitType;
    
    /**
     * 职位名称
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

