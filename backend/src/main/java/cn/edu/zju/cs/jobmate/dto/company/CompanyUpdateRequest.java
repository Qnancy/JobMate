package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.dto.common.UpdateRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import lombok.Builder;
import lombok.Data;

/**
 * Company update request DTO.
 */
@Data
@Builder
public class CompanyUpdateRequest implements UpdateRequest<Company> {

    private String name;
    private CompanyType type;

    @Override
    public boolean isUpdatable() {
        return name != null || type != null;
    }

    @Override
    public void apply(Company company) {
        company.setName(name);
        company.setType(type);
    }
}
