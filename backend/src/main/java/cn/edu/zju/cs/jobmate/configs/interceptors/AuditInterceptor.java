package cn.edu.zju.cs.jobmate.configs.interceptors;

import cn.edu.zju.cs.jobmate.configs.security.filters.AuditFilter;
import cn.edu.zju.cs.jobmate.utils.httpservlet.RequestUtil;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor to trace and audit requests.
 * 
 * @apiNote This interceptor cooperates with {@link AuditFilter}.
 */
@Slf4j
@Component
public class AuditInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) throws Exception {
        log.info("Proceeding request in Security Chain in {} ms",
            RequestUtil.checkTimer(request));
        return true;
    }

    @Override
    public void afterCompletion(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        @Nullable Exception exception
    ) throws Exception {
        log.debug("Completed general request processing in {} ms",
            RequestUtil.checkTimer(request));
    }
}
