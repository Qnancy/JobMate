package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Activity information update request DTO
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityInfoUpdateRequest {
    
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
