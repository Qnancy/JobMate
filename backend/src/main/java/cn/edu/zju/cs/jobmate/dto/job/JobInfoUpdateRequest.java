package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Job information update request DTO
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobInfoUpdateRequest {
    
    @Valid
    @NotNull(message = "Company information cannot be empty")
    private CompanyResponse company;
    
    @NotNull(message = "Recruitment type cannot be empty")
    private RecruitType recruitType;
    
    @NotBlank(message = "Job position name cannot be empty")
    @Size(max = 128, message = "Job position name length cannot exceed 128")
    private String position;
    
    @NotBlank(message = "Link cannot be empty")
    private String link;
    
    @NotBlank(message = "Location cannot be empty")
    private String location;
    
    @NotBlank(message = "Extra information cannot be empty")
    private String extra;
}

