package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
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
     * Keyword search (searches in company_name, position, location)
     */
    private String keyword;
    
    /**
     * Recruitment type
     */
    private RecruitType recruitType;
}
