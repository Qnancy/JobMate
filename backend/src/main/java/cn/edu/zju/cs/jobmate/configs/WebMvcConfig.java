package cn.edu.zju.cs.jobmate.configs;

import cn.edu.zju.cs.jobmate.configs.interceptors.*;
import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.configs.resolvers.PageRequestParamResolver;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final MonitorProperties monitorProperties;
    private final AuditInterceptor auditInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;
    private final PageRequestParamResolver pageRequestParamResolver;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // Auditing interceptor.
        registry.addInterceptor(Objects.requireNonNull(auditInterceptor))
                .addPathPatterns("/api/**");
        // Rate limiting interceptor.
        if (monitorProperties.getRateLimiter().getEnabled()) {
            registry.addInterceptor(Objects.requireNonNull(rateLimitInterceptor))
                    .addPathPatterns("/api/**");
        }
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageRequestParamResolver);
    }
}
