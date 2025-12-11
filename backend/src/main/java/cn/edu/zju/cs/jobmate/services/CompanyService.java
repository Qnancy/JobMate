package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Company service interface.
 */
public interface CompanyService {

    /**
     * Create a new company.
     *
     * @param dto company creation request dto
     * @return created company
     * @throws BusinessException if company with the same name already exists
     */
    Company create(CompanyCreateRequest dto);

    /**
     * Delete company.
     *
     * @param id company id
     */
    void delete(Integer id);

    /**
     * Update company.
     *
     * @param id company id
     * @param dto company update request dto
     * @return updated company
     * @throws BusinessException if no update is made or company not found
     */
    Company update(Integer id, CompanyUpdateRequest dto);

    /**
     * Get company by id.
     *
     * @param id company id
     * @return retrieved company
     * @throws BusinessException if company not found
     */
    Company getById(Integer id);

    /**
     * Get company by name.
     *
     * @param name company name
     * @return retrieved company
     * @throws BusinessException if company not found
     */
    Company getByName(String name);

    /**
     * Get companies by type.
     *
     * @param type company type
     * @return list of companies
     */
    List<Company> getByType(CompanyType type);

    /**
     * Get all companies.
     *
     * @return list of companies
     */
    List<Company> getAll();

    /**
     * Get all companies matching query conditions with pagination.
     *
     * @param dto company query request dto
     * @return page of companies
     */
    Page<Company> getAll(CompanyQueryRequest dto);
}
