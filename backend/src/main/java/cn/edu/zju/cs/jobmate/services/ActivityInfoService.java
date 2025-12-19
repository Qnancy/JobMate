package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.activity.*;
import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import org.springframework.data.domain.Page;

/**
 * ActivityInfo service interface.
 */
public interface ActivityInfoService {

    /**
     * Create a new activity info.
     *
     * @param dto activity info creation request dto
     * @return created activity info
     * @throws BusinessException if company not found
     */
    ActivityInfo create(ActivityInfoCreateRequest dto);

    /**
     * Delete activity info.
     *
     * @param id activity info id
     */
    void delete(Integer id);

    /**
     * Update activity info.
     *
     * @param id activity info id
     * @param dto activity info update request dto
     * @return updated activity info
     * @throws BusinessException if no updates made, or activity info or company not found
     */
    ActivityInfo update(Integer id, ActivityInfoUpdateRequest dto);

    /**
     * Get activity info by id.
     *
     * @param id activity info id
     * @return retrieved activity info
     * @throws BusinessException if activity info not found
     */
    ActivityInfo getById(Integer id);

    /**
     * Get activity infos with pagination.
     *
     * @param dto page request dto
     * @return page of activity infos
     */
    Page<ActivityInfo> getAll(PageRequest dto);

    /**
     * Query activity infos with pagination and filters.
     *
     * @param dto activity info query request dto
     * @return page of activity infos
     */
    Page<ActivityInfo> query(ActivityInfoQueryRequest dto);
}
