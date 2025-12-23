package cn.edu.zju.cs.jobmate.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Monitoring related properties.
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app.monitor")
public class MonitorProperties {

    @NotNull
    private SlowApi slowApi;

    @NotNull
    private RateLimiter rateLimiter;

    /**
     * Slow API monitoring properties.
     */
    @Data
    public static class SlowApi {

        /**
         * Whether slow API monitoring is enabled.
         */
        @NotNull
        private Boolean enabled;

        /**
         * Threshold in milliseconds to consider an API call as slow.
         */
        @NotNull
        private Long thresholdMs;
    }

    /**
     * Rate limiter properties.
     */
    @Data
    public static class RateLimiter {

        /**
         * Whether rate limiting is enabled.
         */
        @NotNull
        private Boolean enabled;

        /**
         * Time interval in seconds for rate limiting.
         */
        @NotNull
        private Integer limitIntervalS;

        /**
         * Maximum number of requests allowed per time interval.
         */
        @NotNull
        private Integer maxRequestsPerInterval;
    }
}
