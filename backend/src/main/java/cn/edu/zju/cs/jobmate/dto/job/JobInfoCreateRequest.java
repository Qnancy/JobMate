package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Job information creation request DTO
 */
@Data
@Builder
public class JobInfoCreateRequest {
    
    @Valid
    @NotNull(message = "Company information cannot be empty")
    private CompanyResponse company;
    
    @NotNull(message = "Recruitment type cannot be empty")
    private RecruitType recruitType;
    
    @NotBlank(message = "Job position name cannot be empty")
    @Size(max = 128, message = "Job position name length cannot exceed 128")
    private String position;
    
    private String link;
    private String location;
    private String extra;
}
