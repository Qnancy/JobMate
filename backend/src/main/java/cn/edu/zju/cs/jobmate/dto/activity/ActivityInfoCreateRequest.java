package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import jakarta.validation.Valid;
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
    
    @Valid
    @NotNull(message = "Company information cannot be empty")
    private CompanyResponse company;
    
    @NotBlank(message = "Activity title cannot be empty")
    private String title;
    
    @NotNull(message = "Activity time cannot be empty")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    
    @NotBlank(message = "Link cannot be empty")
    private String link;
    
    @NotBlank(message = "Location cannot be empty")
    private String location;
    
    @NotBlank(message = "Extra information cannot be empty")
    private String extra;
}
