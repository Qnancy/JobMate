package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.dto.common.UpdateRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
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
        if (name != null) { company.setName(name); }
        if (type != null) { company.setType(type); }
    }

    @Override
    public String toString() {
        return "CompanyUpdateRequest{" +
                "name=" + ToStringUtil.wrap(name) +
                ", type=" + type +
                '}';
    }
}
