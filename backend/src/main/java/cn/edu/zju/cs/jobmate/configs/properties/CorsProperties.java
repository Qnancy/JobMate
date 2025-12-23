package cn.edu.zju.cs.jobmate.configs.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * CORS related properties.
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * Allowed origins for CORS.
     */
    @NotNull
    private List<String> allowedOrigins;

    /**
     * Maximum age (in seconds) for CORS preflight requests(OPTIONS).
     */
    private Long maxAge;
}
