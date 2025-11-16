package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;

/**
 * Company response DTO
 */
public class CompanyResponse {
    
    private Integer id;
    private String name;
    private CompanyType type;

    public CompanyResponse() {
    }

    public CompanyResponse(Integer id, String name, CompanyType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

