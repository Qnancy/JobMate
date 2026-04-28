package cn.edu.zju.cs.jobmate.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA's auditing feature so that entities annotated with
 * {@link org.springframework.data.annotation.CreatedDate} and
 * {@link org.springframework.data.annotation.LastModifiedDate} get their timestamps
 * populated automatically.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
