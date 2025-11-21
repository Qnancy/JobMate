package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ActivityInfo entity.
 */
@Repository
public interface ActivityInfoRepository extends JpaRepository<ActivityInfo, Integer>, JpaSpecificationExecutor<ActivityInfo> {

    /**
     * Find activity infos by company.
     *
     * @param company company entity
     * @return list of activity infos
     */
    List<ActivityInfo> findByCompany(Company company);

    /**
     * Find activity infos by company with pagination.
     *
     * @param company company entity
     * @param pageable pageable
     * @return page of activity infos
     */
    Page<ActivityInfo> findByCompany(Company company, Pageable pageable);

    /**
     * Find activity infos by company id.
     *
     * @param companyId company id
     * @return list of activity infos
     */
    List<ActivityInfo> findByCompanyId(Integer companyId);

    /**
     * Find activity infos by company id with pagination.
     *
     * @param companyId company id
     * @param pageable pageable
     * @return page of activity infos
     */
    Page<ActivityInfo> findByCompanyId(Integer companyId, Pageable pageable);

    /**
     * Find activity infos by city.
     *
     * @param city city name
     * @return list of activity infos
     */
    List<ActivityInfo> findByCity(String city);

    /**
     * Find activity infos by city with pagination.
     *
     * @param city city name
     * @param pageable pageable
     * @return page of activity infos
     */
    Page<ActivityInfo> findByCity(String city, Pageable pageable);

    /**
     * Find activity infos after specified time.
     *
     * @param time time threshold
     * @return list of activity infos
     */
    List<ActivityInfo> findByTimeAfter(LocalDateTime time);

    /**
     * Find activity infos after specified time with pagination.
     *
     * @param time time threshold
     * @param pageable pageable
     * @return page of activity infos
     */
    Page<ActivityInfo> findByTimeAfter(LocalDateTime time, Pageable pageable);

    /**
     * Find activity infos before specified time.
     *
     * @param time time threshold
     * @return list of activity infos
     */
    List<ActivityInfo> findByTimeBefore(LocalDateTime time);

    /**
     * Find activity infos before specified time with pagination.
     *
     * @param time time threshold
     * @param pageable pageable
     * @return page of activity infos
     */
    Page<ActivityInfo> findByTimeBefore(LocalDateTime time, Pageable pageable);

    /**
     * Find activity infos between two times.
     *
     * @param startTime start time
     * @param endTime end time
     * @return list of activity infos
     */
    List<ActivityInfo> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find activity infos between two times with pagination.
     *
     * @param startTime start time
     * @param endTime end time
     * @param pageable pageable
     * @return page of activity infos
     */
    Page<ActivityInfo> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * Update activity info by id with new values.
     *
     * @param id activity info id
     * @param companyId company id
     * @param title new title
     * @param time new time
     * @param link new link
     * @param location new location
     * @param extra new extra information
     */
    @Modifying
    @Query("UPDATE ActivityInfo a SET a.company.id = :companyId, a.title = :title, " +
           "a.time = :time, a.link = :link, a.location = :location, a.extra = :extra WHERE a.id = :id")
    void updateActivityInfoById(@Param("id") Integer id, 
                               @Param("companyId") Integer companyId, 
                               @Param("title") String title, 
                               @Param("time") LocalDateTime time, 
                               @Param("link") String link, 
                               @Param("location") String location, 
                               @Param("extra") String extra);
}

