package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * DAO for {@link JobInfo}.
 */
@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Integer>, JpaSpecificationExecutor<JobInfo> {
}
