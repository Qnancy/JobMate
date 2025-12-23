package cn.edu.zju.cs.jobmate.configs.interceptors;

import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor to log requests.
 */
@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    private MonitorProperties.SlowApi slowApi; // Slow API monitoring properties.
    private static final String timeAttribute = "REQUEST_START_TIME";

    public LogInterceptor(MonitorProperties monitorProperties) {
        this.slowApi = monitorProperties.getSlowApi();
    }

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(timeAttribute, startTime);
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId); // Add trace id to MDC for logging.
        log.info("Receiving request from {}: {} {}", ip, method, uri);
        return true;
    }

    @Override
    public void afterCompletion(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        @Nullable Exception exception
    ) throws Exception {
        long startTime = (Long) request.getAttribute(timeAttribute);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String uri = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();
        String ip = request.getRemoteAddr();
        // TODO: get user auth info from SecurityContext
        if (exception != null) {
            log.error("Request from {}: {} {} completed with exception after {} ms",
                ip, method, uri, duration, exception);
        } else if (slowApi.getEnabled() && duration > slowApi.getThresholdMs()) {
            log.warn("Slow request from {}: {} {} completed in {} ms with status {}",
                ip, method, uri, duration, status);
        } else {
            log.info("Request from {}: {} {} completed in {} ms with status {}",
                ip, method, uri, duration, status);
        }
        MDC.remove("traceId"); // Clean up MDC.
    }
}
