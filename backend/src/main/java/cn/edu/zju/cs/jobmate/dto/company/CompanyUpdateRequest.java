package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 公司更新请求DTO
 * 所有字段都是可选的
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

