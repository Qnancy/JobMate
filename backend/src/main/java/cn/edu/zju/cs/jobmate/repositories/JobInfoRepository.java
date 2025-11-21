package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for JobInfo entity.
 */
@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Integer>, JpaSpecificationExecutor<JobInfo> {

    /**
     * Find job infos by company.
     *
     * @param company company entity
     * @return list of job infos
     */
    List<JobInfo> findByCompany(Company company);

    /**
     * Find job infos by company with pagination.
     *
     * @param company company entity
     * @param pageable pageable
     * @return page of job infos
     */
    Page<JobInfo> findByCompany(Company company, Pageable pageable);

    /**
     * Find job infos by company id.
     *
     * @param companyId company id
     * @return list of job infos
     */
    List<JobInfo> findByCompanyId(Integer companyId);

    /**
     * Find job infos by company id with pagination.
     *
     * @param companyId company id
     * @param pageable pageable
     * @return page of job infos
     */
    Page<JobInfo> findByCompanyId(Integer companyId, Pageable pageable);

    /**
     * Find job infos by recruit type.
     *
     * @param recruitType recruit type
     * @return list of job infos
     */
    List<JobInfo> findByRecruitType(RecruitType recruitType);

    /**
     * Find job infos by recruit type with pagination.
     *
     * @param recruitType recruit type
     * @param pageable pageable
     * @return page of job infos
     */
    Page<JobInfo> findByRecruitType(RecruitType recruitType, Pageable pageable);

    /**
     * Find job infos by city.
     *
     * @param city city name
     * @return list of job infos
     */
    List<JobInfo> findByCity(String city);

    /**
     * Find job infos by city with pagination.
     *
     * @param city city name
     * @param pageable pageable
     * @return page of job infos
     */
    Page<JobInfo> findByCity(String city, Pageable pageable);

    /**
     * Find job infos by company and recruit type.
     *
     * @param company company entity
     * @param recruitType recruit type
     * @return list of job infos
     */
    List<JobInfo> findByCompanyAndRecruitType(Company company, RecruitType recruitType);

    /**
     * Find job infos by company and recruit type with pagination.
     *
     * @param company company entity
     * @param recruitType recruit type
     * @param pageable pageable
     * @return page of job infos
     */
    Page<JobInfo> findByCompanyAndRecruitType(Company company, RecruitType recruitType, Pageable pageable);

    /**
     * Update job info by id with new values.
     *
     * @param id job info id
     * @param companyId company id
     * @param recruitType new recruit type
     * @param position new position
     * @param link new link
     * @param location new location
     * @param extra new extra information
     */
    @Modifying
    @Query("UPDATE JobInfo j SET j.company.id = :companyId, j.recruitType = :recruitType, " +
           "j.position = :position, j.link = :link, j.location = :location, j.extra = :extra WHERE j.id = :id")
    void updateJobInfoById(@Param("id") Integer id, 
                          @Param("companyId") Integer companyId, 
                          @Param("recruitType") RecruitType recruitType, 
                          @Param("position") String position, 
                          @Param("link") String link, 
                          @Param("location") String location, 
                          @Param("extra") String extra);
}

