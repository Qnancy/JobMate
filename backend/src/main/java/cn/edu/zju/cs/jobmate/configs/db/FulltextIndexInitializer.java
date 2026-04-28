package cn.edu.zju.cs.jobmate.configs.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures the MySQL 8 {@code FULLTEXT … WITH PARSER ngram} indexes used by
 * keyword search exist on {@code job_infos}, {@code activity_infos} and
 * {@code companies}. Runs once at startup and is idempotent — it queries
 * {@code information_schema.STATISTICS} first and only issues
 * {@code ALTER TABLE} when the named index is missing.
 *
 * <p>We do this in code rather than in {@code init.sql} because the schema is
 * created by Hibernate ({@code ddl-auto: update}) <i>after</i> the init script
 * runs, so the tables don't exist yet at init time.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FulltextIndexInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensureFulltextIndex("job_infos", "ft_job_search", "position, location, extra");
            ensureFulltextIndex("activity_infos", "ft_activity_search", "title, location, extra");
            ensureFulltextIndex("companies", "ft_company_search", "name");
        } catch (Exception e) {
            // Don't crash the app if the user is running on a non-MySQL DB or
            // missing privileges; just log and let the search fall back gracefully.
            log.warn("Failed to ensure fulltext indexes; keyword search may be slower. Cause: {}", e.getMessage());
        }
    }

    private void ensureFulltextIndex(String table, String indexName, String columnList) {
        Integer existing = jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.STATISTICS " +
                "WHERE table_schema = DATABASE() AND table_name = ? AND index_name = ?",
            Integer.class, table, indexName
        );
        if (existing != null && existing > 0) {
            log.info("Fulltext index {}.{} already exists — skipping.", table, indexName);
            return;
        }
        // Table/index/column names are hard-coded above, no SQL injection surface.
        String ddl = String.format(
            "ALTER TABLE %s ADD FULLTEXT INDEX %s (%s) WITH PARSER ngram",
            table, indexName, columnList
        );
        log.info("Creating fulltext index: {}", ddl);
        jdbc.execute(ddl);
        log.info("Fulltext index {}.{} created.", table, indexName);
    }
}
