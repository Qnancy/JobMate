package cn.edu.zju.cs.jobmate.configs.security.filters;

import cn.edu.zju.cs.jobmate.configs.interceptors.AuditInterceptor;
import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
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

    private final MonitorProperties.SlowApi slowApi;

    public AuditFilter(MonitorProperties monitorProperties) {
        this.slowApi = monitorProperties.getSlowApi();
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        // Request info.
        String info = RequestUtil.info(request);
        // Set trace id for logging.
        MDC.put("traceId", UUID.randomUUID().toString());
        // Set start time.
        RequestUtil.setTimer(request);
        log.info("Incoming request: {}", info);

        try {
            // Proceed with the next filter in the chain.
            filterChain.doFilter(request, response);
            // Log the request completion.
            long time = RequestUtil.checkTimer(request);
            if (slowApi.getEnabled() && time > slowApi.getThresholdMs()) {
                log.warn("Slow request {} completed in {} ms with status {}",
                info, time, response.getStatus());
            } else {
                log.info("Request {} completed in {} ms with status {}",
                    info, time, response.getStatus());
            }
        } catch (Exception e) {
            // Log the exception and rethrow it.
            log.error("Request {} completed with exception in {} ms",
                info, RequestUtil.checkTimer(request), e);
            throw e;
        } finally {
            // Request processing completed.
            MDC.remove("traceId");
        }
    }
}
