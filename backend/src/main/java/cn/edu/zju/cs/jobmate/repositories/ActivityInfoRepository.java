package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ActivityInfo entity.
 */
@Repository
public interface ActivityInfoRepository extends JpaRepository<ActivityInfo, Integer> {

    /**
     * Find activity infos by company.
     *
     * @param company company entity
     * @return list of activity infos
     */
    List<ActivityInfo> findByCompany(Company company);

    /**
     * Find activity infos by company id.
     *
     * @param companyId company id
     * @return list of activity infos
     */
    List<ActivityInfo> findByCompanyId(Integer companyId);

    /**
     * Find activity infos by city.
     *
     * @param city city name
     * @return list of activity infos
     */
    List<ActivityInfo> findByCity(String city);

    /**
     * Find activity infos after specified time.
     *
     * @param time time threshold
     * @return list of activity infos
     */
    List<ActivityInfo> findByTimeAfter(LocalDateTime time);

    /**
     * Find activity infos before specified time.
     *
     * @param time time threshold
     * @return list of activity infos
     */
    List<ActivityInfo> findByTimeBefore(LocalDateTime time);

    /**
     * Find activity infos between two times.
     *
     * @param startTime start time
     * @param endTime end time
     * @return list of activity infos
     */
    List<ActivityInfo> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}

