package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

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
     * Keyword search (can search activity title, company name, etc.)
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
     * City
     */
    private String city;
    
    /**
     * Activity start time (query activities after this time)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * Activity end time (query activities before this time)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
