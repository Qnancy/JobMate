package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Company update request DTO
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyUpdateRequest {

    @NotBlank(message = "Company name cannot be empty")
    private String name;
    
    @NotNull(message = "Company type cannot be empty")
    private CompanyType type;
}

