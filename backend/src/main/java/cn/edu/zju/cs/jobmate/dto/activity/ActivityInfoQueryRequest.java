package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Activity information query request DTO
 * Supports optional parameter queries
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityInfoQueryRequest extends PageRequest {
    
    /**
     * Keyword search (searches in title, company_name)
     */
    private String keyword;
}
