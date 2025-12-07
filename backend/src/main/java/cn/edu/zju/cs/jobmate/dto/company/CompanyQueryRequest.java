package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Company pagination query request DTO.
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CompanyQueryRequest extends PageRequest {

    private CompanyType type;
}
