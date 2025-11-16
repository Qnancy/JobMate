package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Activity information query request DTO
 * Supports optional parameter queries
 */
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

    public ActivityInfoQueryRequest() {
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

