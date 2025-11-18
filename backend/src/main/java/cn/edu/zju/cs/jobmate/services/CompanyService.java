package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;

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
     * Get companies by type.
     *
     * @param type company type
     * @return list of companies
     */
    List<Company> getByType(CompanyType type);

    /**
     * Update company.
     *
     * @param company company to update
     * @return updated company
     */
    Company update(Company company);

    /**
     * Delete company by id.
     *
     * @param id company id
     */
    void deleteById(Integer id);

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

