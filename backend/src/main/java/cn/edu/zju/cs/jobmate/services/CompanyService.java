package cn.edu.zju.cs.jobmate.services;

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
     * @param company company to create
     * @return created company
     * @throws BusinessException if company with the same name already exists
     */
    Company create(Company company);

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
     * @param name new company name
     * @param type new company type
     * @return updated company
     * @throws BusinessException if no update is made or company not found
     */
    Company update(Integer id, String name, CompanyType type);

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
     * Get companies by type with pagination.
     *
     * @param type company type
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of companies
     */
    Page<Company> getByType(CompanyType type, Integer page, Integer pageSize);

    /**
     * Get all companies.
     *
     * @return list of companies
     */
    List<Company> getAll();

    /**
     * Get all companies with pagination.
     *
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of companies
     */
    Page<Company> getAll(Integer page, Integer pageSize);
}
