package cn.edu.zju.cs.jobmate.repositories;

import cn.edu.zju.cs.jobmate.models.JobInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * DAO for {@link JobInfo}.
 */
@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Long>, JpaSpecificationExecutor<JobInfo> {

    /**
     * Full-text keyword search backed by MySQL 8 ngram FULLTEXT indexes on
     * {@code job_infos(position, location, extra)} and {@code companies(name)}.
     *
     * <p>Uses BOOLEAN MODE so that very common ngram tokens are not silently
     * dropped (Natural Language mode applies a 50% document-frequency cutoff).
     * Additionally ORs case-insensitive {@code LIKE} on the same fields so short
     * English tokens (e.g. {@code java} in {@code Java 后端开发工程师}) still hit
     * when ngram BOOLEAN matching is unreliable on mixed CJK/Latin text.
     *
     * @param keyword      raw user input for FULLTEXT BOOLEAN MODE
     * @param likeKw       {@code LIKE} pattern body (without {@code %}), with
     *                     {@code % _ ! \\} escaped for use with {@code ESCAPE '!'}
     * @param recruitType  optional recruit-type filter (enum name as string),
     *                     pass {@code null} to disable the filter
     */
    @Query(
        value = """
            SELECT j.* FROM job_infos j
            JOIN companies c ON j.company_id = c.id
            WHERE (
                MATCH(j.position, j.location, j.extra) AGAINST (:kw IN BOOLEAN MODE)
                OR MATCH(c.name) AGAINST (:kw IN BOOLEAN MODE)
                OR LOWER(j.position) LIKE LOWER(CONCAT('%', :likeKw, '%')) ESCAPE '!'
                OR LOWER(IFNULL(j.extra, '')) LIKE LOWER(CONCAT('%', :likeKw, '%')) ESCAPE '!'
                OR LOWER(IFNULL(j.location, '')) LIKE LOWER(CONCAT('%', :likeKw, '%')) ESCAPE '!'
                OR LOWER(c.name) LIKE LOWER(CONCAT('%', :likeKw, '%')) ESCAPE '!'
            )
            AND (:recruitType IS NULL OR j.recruit_type = :recruitType)
            AND (:companyId IS NULL OR j.company_id = :companyId)
            ORDER BY (
                MATCH(j.position, j.location, j.extra) AGAINST (:kw IN BOOLEAN MODE)
                + IFNULL(MATCH(c.name) AGAINST (:kw IN BOOLEAN MODE), 0)
            ) DESC, j.created_at DESC
            """,
        countQuery = """
            SELECT COUNT(*) FROM job_infos j
            JOIN companies c ON j.company_id = c.id
            WHERE (
                MATCH(j.position, j.location, j.extra) AGAINST (:kw IN BOOLEAN MODE)
                OR MATCH(c.name) AGAINST (:kw IN BOOLEAN MODE)
                OR LOWER(j.position) LIKE LOWER(CONCAT('%', :likeKw, '%')) ESCAPE '!'
                OR LOWER(IFNULL(j.extra, '')) LIKE LOWER(CONCAT('%', :likeKw, '%')) ESCAPE '!'
                OR LOWER(IFNULL(j.location, '')) LIKE LOWER(CONCAT('%', :likeKw, '%')) ESCAPE '!'
                OR LOWER(c.name) LIKE LOWER(CONCAT('%', :likeKw, '%')) ESCAPE '!'
            )
            AND (:recruitType IS NULL OR j.recruit_type = :recruitType)
            AND (:companyId IS NULL OR j.company_id = :companyId)
            """,
        nativeQuery = true
    )
    Page<JobInfo> searchByFulltext(
        @Param("kw") String keyword,
        @Param("likeKw") String likeKeyword,
        @Param("recruitType") String recruitType,
        @Param("companyId") Long companyId,
        Pageable pageable
    );
}
