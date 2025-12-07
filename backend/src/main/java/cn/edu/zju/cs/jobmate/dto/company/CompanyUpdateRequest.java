package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import lombok.Builder;
import lombok.Data;

/**
 * Company update request DTO.
 */
@Data
@Builder
public class CompanyUpdateRequest {

    private String name;
    private CompanyType type;
}
