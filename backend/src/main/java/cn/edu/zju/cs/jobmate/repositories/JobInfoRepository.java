package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for JobInfo entity.
 */
@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Integer> {

    /**
     * Find job infos by company.
     *
     * @param company company entity
     * @return list of job infos
     */
    List<JobInfo> findByCompany(Company company);

    /**
     * Find job infos by company id.
     *
     * @param companyId company id
     * @return list of job infos
     */
    List<JobInfo> findByCompanyId(Integer companyId);

    /**
     * Find job infos by recruit type.
     *
     * @param recruitType recruit type
     * @return list of job infos
     */
    List<JobInfo> findByRecruitType(RecruitType recruitType);

    /**
     * Find job infos by city.
     *
     * @param city city name
     * @return list of job infos
     */
    List<JobInfo> findByCity(String city);

    /**
     * Find job infos by company and recruit type.
     *
     * @param company company entity
     * @param recruitType recruit type
     * @return list of job infos
     */
    List<JobInfo> findByCompanyAndRecruitType(Company company, RecruitType recruitType);
}

