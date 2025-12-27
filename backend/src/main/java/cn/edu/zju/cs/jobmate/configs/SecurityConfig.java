package cn.edu.zju.cs.jobmate.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.configs.properties.CorsProperties;
import cn.edu.zju.cs.jobmate.configs.security.filters.*;
import cn.edu.zju.cs.jobmate.configs.security.handlers.*;
import cn.edu.zju.cs.jobmate.configs.security.jwt.JwtTokenProvider;
import cn.edu.zju.cs.jobmate.utils.security.SecurityResponder;
import lombok.RequiredArgsConstructor;

/**
 * Security configuration for JobMate.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties cors;
    private final AuthEntryPoint authEntryPoint;
    private final AccessDenier accessDenier;

    private final ObjectMapper mapper;
    private final SecurityResponder responder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    
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
     * Authentication manager used in authentication processes.
     * 
     * @param config AuthenticationConfiguration instance
     * @return AuthenticationManager instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
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
        conf.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        conf.addAllowedHeader("*"); // TODO: tighten this.
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterAt(
                new LoginAuthenticationFilter(
                    authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)),
                    responder,
                    mapper,
                    jwtTokenProvider
                ),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterAfter(
                new JwtAuthenticationFilter(
                    responder,
                    jwtTokenProvider,
                    userDetailsService
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
