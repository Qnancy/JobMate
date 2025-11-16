package cn.edu.zju.cs.jobmate.dto.activity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Activity information update request DTO
 * All fields are optional
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityInfoUpdateRequest {
    
    private Integer companyId;
    private String title;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    
    private String link;
    private String city;
    private String location;
    private String extra;
}
