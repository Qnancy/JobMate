package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Company creation request DTO
 */
public class CompanyCreateRequest {
    
    @NotBlank(message = "Company name cannot be empty")
    private String name;
    
    @NotNull(message = "Company type cannot be empty")
    private CompanyType type;

    public CompanyCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CompanyType getType() {
        return type;
    }

    public void setType(CompanyType type) {
        this.type = type;
    }
}

