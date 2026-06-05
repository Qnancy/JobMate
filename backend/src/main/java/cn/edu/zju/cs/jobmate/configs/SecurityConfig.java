package cn.edu.zju.cs.jobmate.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.configs.properties.CorsProperties;
import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.configs.properties.AdminProperties;
import cn.edu.zju.cs.jobmate.configs.security.filters.*;
import cn.edu.zju.cs.jobmate.configs.security.handlers.*;
import cn.edu.zju.cs.jobmate.security.jwt.JwtBlacklistManager;
import cn.edu.zju.cs.jobmate.security.jwt.JwtTokenProvider;
import cn.edu.zju.cs.jobmate.utils.httpservlet.ResponseUtil;
import lombok.RequiredArgsConstructor;

/**
 * Security configuration for JobMate.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties cors;
    private final AuthEntryPoint authEntryPoint;
    private final AccessDenier accessDenier;

    private final ObjectMapper mapper;
    private final ResponseUtil responder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtBlacklistManager jwtBlacklistManager;
    private final MonitorProperties monitorProperties;
    private final AdminProperties adminProperties;
    
    /**
     * Password encoder to validate user passwords.
     * 
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 登录认证：对用户不存在与密码错误区分异常类型（{@link DaoAuthenticationProvider#setHideUserNotFoundExceptions}）。
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        provider.setHideUserNotFoundExceptions(false);
        return new ProviderManager(provider);
    }

    /**
     * CORS configuration.
     * 
     * @return CorsConfigurationSource instance
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOrigins(cors.getAllowedOrigins());
        conf.setAllowedMethods(List.of(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
        ));
        conf.setAllowedHeaders(List.of(
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.CONTENT_TYPE
        ));
        conf.setAllowCredentials(true);
        conf.setMaxAge(cors.getMaxAge());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", conf);
        return source;
    }

    /**
     * Security filter chain configuration.
     * 
     * @param http HttpSecurity instance
     * @return SecurityFilterChain instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/monitor/**").permitAll() // TODO: secure this endpoint
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new AuditFilter(monitorProperties),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterAt(
                new LoginAuthenticationFilter(
                    authenticationManager,
                    responder,
                    mapper,
                    jwtTokenProvider,
                    adminProperties
                ),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterAfter(
                new JwtAuthenticationFilter(
                    responder,
                    jwtTokenProvider,
                    jwtBlacklistManager
                ),
                UsernamePasswordAuthenticationFilter.class
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDenier)
            );
        return http.build();
    }
}
