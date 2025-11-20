package cn.edu.zju.cs.jobmate.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

/**
 * Monitoring related properties.
 */
@Component
@Validated
@ConfigurationProperties(prefix = "app.monitor")
public class MonitorProperties {

    @NotNull
    private SlowApi slowApi;

    @NotNull
    private RateLimiter rateLimiter;

    public SlowApi getSlowApi() {
        return slowApi;
    }

    public void setSlowApi(SlowApi slowApi) {
        this.slowApi = slowApi;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * Slow API monitoring properties.
     */
    public static class SlowApi {

        @NotNull
        private Boolean enabled;

        @NotNull
        private Long thresholdMs;

        public Boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Long getThresholdMs() {
            return thresholdMs;
        }

        public void setThresholdMs(Long thresholdMs) {
            this.thresholdMs = thresholdMs;
        }
    }

    /**
     * Rate limiter properties.
     */
    public static class RateLimiter {

        @NotNull
        private Boolean enabled;

        @NotNull
        private Integer limitIntervalS;

        @NotNull
        private Integer maxRequestsPerInterval;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getLimitIntervalS() {
            return limitIntervalS;
        }

        public void setLimitIntervalS(Integer limitIntervalS) {
            this.limitIntervalS = limitIntervalS;
        }

        public Integer getMaxRequestsPerInterval() {
            return maxRequestsPerInterval;
        }

        public void setMaxRequestsPerInterval(Integer maxRequestsPerInterval) {
            this.maxRequestsPerInterval = maxRequestsPerInterval;
        }
    }
}
