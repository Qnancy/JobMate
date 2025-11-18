package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for JobInfo entity.
 */
public interface JobInfoService {

    /**
     * Create a new job info.
     *
     * @param jobInfo job info to create
     * @return created job info
     */
    JobInfo create(JobInfo jobInfo);

    /**
     * Get job info by id.
     *
     * @param id job info id
     * @return optional job info
     */
    Optional<JobInfo> getById(Integer id);

    /**
     * Get all job infos.
     *
     * @return list of job infos
     */
    List<JobInfo> getAll();

    /**
     * Get job infos with pagination.
     *
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of job infos
     */
    Page<JobInfo> getAll(Integer page, Integer pageSize);

    /**
     * Get job infos by company.
     *
     * @param company company entity
     * @return list of job infos
     */
    List<JobInfo> getByCompany(Company company);

    /**
     * Get job infos by company id.
     *
     * @param companyId company id
     * @return list of job infos
     */
    List<JobInfo> getByCompanyId(Integer companyId);

    /**
     * Get job infos by company with pagination.
     *
     * @param company company entity
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of job infos
     */
    Page<JobInfo> getByCompany(Company company, Integer page, Integer pageSize);

    /**
     * Get job infos by company id with pagination.
     *
     * @param companyId company id
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of job infos
     */
    Page<JobInfo> getByCompanyId(Integer companyId, Integer page, Integer pageSize);

    /**
     * Get job infos by recruit type.
     *
     * @param recruitType recruit type
     * @return list of job infos
     */
    List<JobInfo> getByRecruitType(RecruitType recruitType);

    /**
     * Get job infos by recruit type with pagination.
     *
     * @param recruitType recruit type
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of job infos
     */
    Page<JobInfo> getByRecruitType(RecruitType recruitType, Integer page, Integer pageSize);

    /**
     * Get job infos by city.
     *
     * @param city city name
     * @return list of job infos
     */
    List<JobInfo> getByCity(String city);

    /**
     * Get job infos by city with pagination.
     *
     * @param city city name
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of job infos
     */
    Page<JobInfo> getByCity(String city, Integer page, Integer pageSize);

    /**
     * Get job infos by company and recruit type.
     *
     * @param company company entity
     * @param recruitType recruit type
     * @return list of job infos
     */
    List<JobInfo> getByCompanyAndRecruitType(Company company, RecruitType recruitType);

    /**
     * Get job infos by company and recruit type with pagination.
     *
     * @param company company entity
     * @param recruitType recruit type
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of job infos
     */
    Page<JobInfo> getByCompanyAndRecruitType(Company company, RecruitType recruitType, Integer page, Integer pageSize);

    /**
     * Update job info.
     *
     * @param jobInfo job info to update
     * @return updated job info
     */
    JobInfo update(JobInfo jobInfo);

    /**
     * Delete job info by id.
     *
     * @param id job info id
     */
    void deleteById(Integer id);

    /**
     * Check if job info exists by id.
     *
     * @param id job info id
     * @return true if exists
     */
    boolean existsById(Integer id);
}

