package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO for {@link Company}.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    /**
     * Find company by name.
     *
     * @param name company name
     * @return optional company
     */
    Optional<Company> findByName(String name);

    /**
     * Find companies by type.
     *
     * @param type company type
     * @return list of companies
     */
    List<Company> findByType(CompanyType type);

    /**
     * Find companies by type with pagination.
     *
     * @param type company type
     * @param pageable pageable
     * @return page of companies
     */
    Page<Company> findByType(CompanyType type, Pageable pageable);

    /**
     * Check if company exists by name.
     *
     * @param name company name
     * @return true if exists
     */
    boolean existsByName(String name);
}
