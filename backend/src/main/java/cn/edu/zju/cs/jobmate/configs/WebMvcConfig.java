package cn.edu.zju.cs.jobmate.configs;

import cn.edu.zju.cs.jobmate.configs.interceptors.*;
import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.configs.properties.WebMvcProperties;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration using {@link WebMvcProperties}.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Autowired
    private MonitorProperties monitorProperties;

    @Autowired
    private LogInterceptor logInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        WebMvcProperties.Cors cors = webMvcProperties.getCors();
        registry.addMapping("/api/**")
                .allowedOrigins(Objects.requireNonNull(cors.getAllowedOrigins()))
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(cors.getMaxAge());
    }

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
