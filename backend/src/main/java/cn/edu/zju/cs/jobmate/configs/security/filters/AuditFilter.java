package cn.edu.zju.cs.jobmate.configs.security.filters;

import cn.edu.zju.cs.jobmate.configs.interceptors.AuditInterceptor;
import cn.edu.zju.cs.jobmate.utils.httpservlet.RequestUtil;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Audit filter for logging and tracing request details.
 * 
 * @apiNote This filter cooperates with {@link AuditInterceptor}.
 */
@Slf4j
public class AuditFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        // Set trace id for logging.
        MDC.put("traceId", UUID.randomUUID().toString());
        // Set start time.
        RequestUtil.setTimer(request);
        log.info("Incoming request: {}", RequestUtil.info(request));
        // Proceed with the next filter in the chain.
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Exception during request processing", e);
            throw e;
        } finally {
            MDC.remove("traceId");
        }
    }
}
