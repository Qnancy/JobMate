package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import lombok.Builder;
import lombok.Data;

/**
 * Company response DTO.
 */
@Data
@Builder
public class CompanyResponse {
    
    private Long id;
    private String name;
    private CompanyType type;

    /**
     * Convert from Company entity to CompanyResponse
     */
    public static CompanyResponse from(Company company) {
        if (company == null) {
            return null;
        }
        return new CompanyResponse(
            company.getId(),
            company.getName(),
            company.getType()
        );
    }
}
