package cn.edu.zju.cs.jobmate.configs;

import cn.edu.zju.cs.jobmate.configs.interceptors.*;
import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private MonitorProperties monitorProperties;

    @Autowired
    private LogInterceptor logInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // Logging interceptor
        registry.addInterceptor(Objects.requireNonNull(logInterceptor))
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/login",
                    "/api/register",
                    "/api/health"
                );
        // Rate limiting interceptor
        if (monitorProperties.getRateLimiter().isEnabled()) {
            registry.addInterceptor(Objects.requireNonNull(rateLimitInterceptor))
                    .addPathPatterns("/api/**");
        }
    }
}
