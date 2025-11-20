package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for ActivityInfo entity.
 */
public interface ActivityInfoService {

    /**
     * Create a new activity info.
     *
     * @param activityInfo activity info to create
     * @return created activity info
     */
    ActivityInfo create(ActivityInfo activityInfo);

    /**
     * Get activity info by id.
     *
     * @param id activity info id
     * @return optional activity info
     */
    Optional<ActivityInfo> getById(Integer id);

    /**
     * Get all activity infos.
     *
     * @return list of activity infos
     */
    List<ActivityInfo> getAll();

    /**
     * Get activity infos with pagination.
     *
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of activity infos
     */
    Page<ActivityInfo> getAll(Integer page, Integer pageSize);

    /**
     * Get activity infos by company.
     *
     * @param company company entity
     * @return list of activity infos
     */
    List<ActivityInfo> getByCompany(Company company);

    /**
     * Get activity infos by company id.
     *
     * @param companyId company id
     * @return list of activity infos
     */
    List<ActivityInfo> getByCompanyId(Integer companyId);

    /**
     * Get activity infos by company with pagination.
     *
     * @param company company entity
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of activity infos
     */
    Page<ActivityInfo> getByCompany(Company company, Integer page, Integer pageSize);

    /**
     * Get activity infos by company id with pagination.
     *
     * @param companyId company id
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of activity infos
     */
    Page<ActivityInfo> getByCompanyId(Integer companyId, Integer page, Integer pageSize);

    /**
     * Get activity infos by city.
     *
     * @param city city name
     * @return list of activity infos
     */
    List<ActivityInfo> getByCity(String city);

    /**
     * Get activity infos by city with pagination.
     *
     * @param city city name
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of activity infos
     */
    Page<ActivityInfo> getByCity(String city, Integer page, Integer pageSize);

    /**
     * Get activity infos after specified time.
     *
     * @param time time threshold
     * @return list of activity infos
     */
    List<ActivityInfo> getByTimeAfter(LocalDateTime time);

    /**
     * Get activity infos after specified time with pagination.
     *
     * @param time time threshold
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of activity infos
     */
    Page<ActivityInfo> getByTimeAfter(LocalDateTime time, Integer page, Integer pageSize);

    /**
     * Get activity infos before specified time.
     *
     * @param time time threshold
     * @return list of activity infos
     */
    List<ActivityInfo> getByTimeBefore(LocalDateTime time);

    /**
     * Get activity infos before specified time with pagination.
     *
     * @param time time threshold
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of activity infos
     */
    Page<ActivityInfo> getByTimeBefore(LocalDateTime time, Integer page, Integer pageSize);

    /**
     * Get activity infos between two times.
     *
     * @param startTime start time
     * @param endTime end time
     * @return list of activity infos
     */
    List<ActivityInfo> getByTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Get activity infos between two times with pagination.
     *
     * @param startTime start time
     * @param endTime end time
     * @param page page number (0-based)
     * @param pageSize page size
     * @return page of activity infos
     */
    Page<ActivityInfo> getByTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer pageSize);

    /**
     * Update activity info.
     *
     * @param activityInfo activity info to update
     * @return updated activity info
     */
    ActivityInfo update(ActivityInfo activityInfo);

    /**
     * Delete activity info by id.
     *
     * @param id activity info id
     */
    void deleteById(Integer id);

    /**
     * Check if activity info exists by id.
     *
     * @param id activity info id
     * @return true if exists
     */
    boolean existsById(Integer id);

    /**
     * Query activity infos with pagination and filters.
     *
     * @param keyword keyword to search in title, company_name, optional
     * @param page page number (0-based), optional
     * @param pageSize page size, optional
     * @return page of activity infos
     */
    Page<ActivityInfo> query(String keyword, Integer page, Integer pageSize);
}

