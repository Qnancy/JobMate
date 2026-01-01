package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.job.*;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.models.JobInfo;

import org.springframework.data.domain.Page;

/**
 * JobInfo service interface.
 */
public interface JobInfoService {

    /**
     * Create a new job info.
     *
     * @param dto job info creation request dto
     * @return created job info
     * @throws BusinessException if company not found
     */
    JobInfo create(JobInfoCreateRequest dto);

    /**
     * Delete job info.
     *
     * @param id job info id
     */
    void delete(Long id);

    /**
     * Update job info.
     *
     * @param id job info id
     * @param dto job info update request dto
     * @return updated job info
     * @throws BusinessException if no updates made, or job info or company not found
     */
    JobInfo update(Long id, JobInfoUpdateRequest dto);

    /**
     * Get job info by id.
     *
     * @param id job info id
     * @return retrieved job info
     * @throws BusinessException if job info not found
     */
    JobInfo getById(Long id);

    /**
     * Get job infos with pagination.
     *
     * @param dto page request dto
     * @return page of job infos
     */
    Page<JobInfo> getAll(PageRequest dto);

    /**
     * Query job infos with pagination and filters.
     *
     * @param dto job info query request dto
     * @return page of job infos
     */
    Page<JobInfo> query(JobInfoQueryRequest dto);
}
