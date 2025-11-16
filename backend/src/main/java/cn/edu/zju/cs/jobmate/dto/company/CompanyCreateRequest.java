package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 公司创建请求DTO
 */
public class CompanyCreateRequest {
    
    @NotBlank(message = "公司名称不能为空")
    private String name;
    
    @NotNull(message = "公司类型不能为空")
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

