package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Job information query request DTO
 * Supports optional parameter queries
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
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
}
