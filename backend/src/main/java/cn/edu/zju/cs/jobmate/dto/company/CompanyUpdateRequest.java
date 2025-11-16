package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Company update request DTO
 * All fields are optional
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyUpdateRequest {
    
    private String name;
    private CompanyType type;

    public CompanyUpdateRequest() {
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

