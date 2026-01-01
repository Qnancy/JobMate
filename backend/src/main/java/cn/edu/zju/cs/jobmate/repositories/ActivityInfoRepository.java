package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * DAO for {@link ActivityInfo}.
 */
@Repository
public interface ActivityInfoRepository extends JpaRepository<ActivityInfo, Long>, JpaSpecificationExecutor<ActivityInfo> {
}
