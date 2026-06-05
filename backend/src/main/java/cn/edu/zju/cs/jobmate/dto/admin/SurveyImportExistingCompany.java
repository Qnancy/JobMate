package cn.edu.zju.cs.jobmate.dto.admin;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import lombok.Builder;
import lombok.Data;

/**
 * Snapshot of an existing company shown when import would overwrite data.
 */
@Data
@Builder
public class SurveyImportExistingCompany {

    private Long id;
    private String name;
    private CompanyType type;
    /** Chinese label for display, e.g. 民营企业 */
    private String typeLabel;
    private String description;

    public static SurveyImportExistingCompany from(Company company) {
        if (company == null) {
            return null;
        }
        CompanyType type = company.getType();
        return SurveyImportExistingCompany.builder()
            .id(company.getId())
            .name(company.getName())
            .type(type)
            .typeLabel(type != null ? companyTypeLabel(type) : null)
            .description(company.getDescription())
            .build();
    }

    private static String companyTypeLabel(CompanyType type) {
        return switch (type) {
            case STATE -> "国企 / 事业单位";
            case PRIVATE -> "民营企业";
            case FOREIGN -> "外资 / 合资";
        };
    }
}
