package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * DAO for {@link ActivityInfo}.
 */
@Repository
public interface ActivityInfoRepository extends JpaRepository<ActivityInfo, Long>, JpaSpecificationExecutor<ActivityInfo> {

    /**
     * Full-text keyword search backed by MySQL 8 ngram FULLTEXT indexes on
     * {@code activity_infos(title, location, extra)} and {@code companies(name)}.
     *
     * <p>Results are ordered by combined relevance, then by activity time
     * descending as a tie-breaker (newest events first).
     */
    @Query(
        value = """
            SELECT a.* FROM activity_infos a
            JOIN companies c ON a.company_id = c.id
            WHERE (
                MATCH(a.title, a.location, a.extra) AGAINST (:kw IN BOOLEAN MODE)
                OR MATCH(c.name) AGAINST (:kw IN BOOLEAN MODE)
            )
            ORDER BY (
                MATCH(a.title, a.location, a.extra) AGAINST (:kw IN BOOLEAN MODE)
                + IFNULL(MATCH(c.name) AGAINST (:kw IN BOOLEAN MODE), 0)
            ) DESC, a.time DESC
            """,
        countQuery = """
            SELECT COUNT(*) FROM activity_infos a
            JOIN companies c ON a.company_id = c.id
            WHERE (
                MATCH(a.title, a.location, a.extra) AGAINST (:kw IN BOOLEAN MODE)
                OR MATCH(c.name) AGAINST (:kw IN BOOLEAN MODE)
            )
            """,
        nativeQuery = true
    )
    Page<ActivityInfo> searchByFulltext(
        @Param("kw") String keyword,
        Pageable pageable
    );
}
