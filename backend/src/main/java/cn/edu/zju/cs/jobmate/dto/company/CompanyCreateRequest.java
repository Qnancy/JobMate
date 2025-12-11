package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.dto.common.CreateRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Company creation request DTO.
 */
@Data
@Builder
public class CompanyCreateRequest implements CreateRequest<Company> {

    @NotBlank(message = "Company name cannot be empty")
    private String name;

    @NotNull(message = "Company type cannot be null")
    private CompanyType type;

    @Override
    public Company toModel() {
        return new Company(name, type);
    }
}
