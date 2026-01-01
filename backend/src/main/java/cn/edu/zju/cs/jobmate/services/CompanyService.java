package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.models.Company;
import org.springframework.data.domain.Page;

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
    void delete(Long id);

    /**
     * Update company.
     *
     * @param id company id
     * @param dto company update request dto
     * @return updated company
     * @throws BusinessException if no update is made or company not found
     */
    Company update(Long id, CompanyUpdateRequest dto);

    /**
     * Get company by id.
     *
     * @param id company id
     * @return retrieved company
     * @throws BusinessException if company not found
     */
    Company getById(Long id);

    /**
     * Get all companies with pagination.
     *
     * @param dto page request dto
     * @return page of companies
     */
    Page<Company> getAll(PageRequest dto);

    /**
     * Query companies with pagination.
     *
     * @param dto company query request dto
     * @return page of companies
     */
    Page<Company> query(CompanyQueryRequest dto);
}
