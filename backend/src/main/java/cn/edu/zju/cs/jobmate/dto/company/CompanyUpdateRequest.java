package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Company update request DTO
 * All fields are optional
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyUpdateRequest {

    private String name;
    private CompanyType type;
}

