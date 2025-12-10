package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Company creation request DTO.
 */
@Data
@Builder
public class CompanyCreateRequest {

    @NotBlank(message = "Company name cannot be empty")
    private String name;

    @NotNull(message = "Company type cannot be null")
    private CompanyType type;
}
