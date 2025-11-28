package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Company entity.
 */
public interface CompanyService {

    /**
     * Create a new company.
     *
     * @param company company to create
     * @return created company
     */
    Company create(Company company);

    /**
     * Get company by id.
     *
     * @param id company id
     * @return optional company
     */
    Optional<Company> getById(Integer id);

    /**
     * Get company by id, throw exception if not found.
     *
     * @param id company id
     * @return company
     * @throws BusinessException if company not found
     */
    Company getCompanyById(Integer id);

    /**
     * Get company by name.
     *
     * @param name company name
     * @return optional company
     */
    Optional<Company> getByName(String name);

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
     * Update company.
     *
     * @param company company to update
     * @return updated company
     */
    Company update(Company company);

    /**
     * Update company by id with new values.
     *
     * @param id company id
     * @param name new company name
     * @param type new company type
     * @return updated company
     */
    Company updateById(Integer id, String name, CompanyType type);

    /**
     * Delete company by id.
     *
     * @param id company id
     */
    void deleteById(Integer id);

    /**
     * Delete company by id, throw exception if not found.
     *
     * @param id company id
     * @throws BusinessException if company not found
     */
    void deleteCompanyById(Integer id);

    /**
     * Check if company exists by id.
     *
     * @param id company id
     * @return true if exists
     */
    boolean existsById(Integer id);

    /**
     * Check if company exists by name.
     *
     * @param name company name
     * @return true if exists
     */
    boolean existsByName(String name);
}

