package cn.edu.zju.cs.jobmate.configs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cn.edu.zju.cs.jobmate.configs.properties.CorsProperties;
import cn.edu.zju.cs.jobmate.configs.security.*;

/**
 * Security configuration for JobMate.
 * 
 * TODO: add OAuth2 support.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CorsProperties cors;

    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Autowired
    private AccessDenier accessDenier;

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
                .requestMatchers("/api/**").permitAll() // TODO: adjust.
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDenier)
            );
        
        // TODO: http.addFilterAfter();
        return http.build();
    }

    //@Bean
    public UserDetailsService userDetailsService() {
        return null; // TODO
    }
}
