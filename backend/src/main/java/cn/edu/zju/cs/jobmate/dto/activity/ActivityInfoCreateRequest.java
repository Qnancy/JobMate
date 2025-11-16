package cn.edu.zju.cs.jobmate.dto.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Activity information creation request DTO
 */
@Data
@Builder
public class ActivityInfoCreateRequest {
    
    @NotNull(message = "Company ID cannot be empty")
    private Integer companyId;
    
    @NotBlank(message = "Activity title cannot be empty")
    private String title;
    
    @NotNull(message = "Activity time cannot be empty")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    
    private String link;
    private String city;
    private String location;
    private String extra;

}
