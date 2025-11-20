package cn.edu.zju.cs.jobmate.configs.interceptors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor to log requests.
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Value("${monitor.slow-api-threshold-ms}")
    private long slowApiThresholdMs;

    private final String timeAttribute = "REQUEST_START_TIME";

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(timeAttribute, startTime);
        //String uri = request.getRequestURI();
        //String method = request.getMethod();
        // TODO: log request info here
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
        //String uri = request.getRequestURI();
        //String method = request.getMethod();
        //int status = response.getStatus();
        //String clientIp = request.getRemoteAddr();
        // TODO: get user auth info from SecurityContext
        if (exception != null) {
            // TODO: log exception info here
        } else if (duration > slowApiThresholdMs) {
            // TODO: log slow requests here
        } else {
            // TODO: log normal requests here
        }
    }
}
