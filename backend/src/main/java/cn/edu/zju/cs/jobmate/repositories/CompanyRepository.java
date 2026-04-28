package cn.edu.zju.cs.jobmate.repositories;

import java.util.Optional;

import cn.edu.zju.cs.jobmate.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * DAO for {@link Company}.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    /**
     * Check if company exists by name.
     *
     * @param name company name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Find company by exact name match.
     *
     * @param name company name
     * @return optional company
     */
    Optional<Company> findByName(String name);
}
