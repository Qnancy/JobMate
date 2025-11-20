package cn.edu.zju.cs.jobmate.configs.interceptors;

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
public class LogInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("REQUEST_START_TIME", startTime);
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
        long startTime = (Long) request.getAttribute("REQUEST_START_TIME");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        //String uri = request.getRequestURI();
        //String method = request.getMethod();
        //int status = response.getStatus();
        //String clientIp = request.getRemoteAddr();
        // TODO: get user auth info from SecurityContext
        if (exception != null) {
            // TODO: log exception info here
        } else if (duration > 1000) {
            // TODO: log slow requests here
        } else {
            // TODO: log normal requests here
        }
    }
}
