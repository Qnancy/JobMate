package cn.edu.zju.cs.jobmate.configs.interceptors;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor to limit request rate.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private StringRedisTemplate redis;
    private MonitorProperties.RateLimiter rateLimiter;
    private static final String PREFIX = "rate_limit:";

    public RateLimitInterceptor(
        StringRedisTemplate redis,
        MonitorProperties monitorProperties
    ) {
        this.redis = redis;
        this.rateLimiter = monitorProperties.getRateLimiter();
    }

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) throws Exception {
        // TODO: get user from security context
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String key = PREFIX + ip + ":" + uri;
        Long cnt = redis.opsForValue().increment(key);
        // Set expiration for the key on first increment
        if (cnt != null && cnt == 1) {
            redis.expire(key, rateLimiter.getLimitIntervalS(), TimeUnit.SECONDS);
        }
        // Too many requests
        if (cnt != null && cnt > rateLimiter.getMaxRequestsPerInterval()) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }
        return true;
    }
}
