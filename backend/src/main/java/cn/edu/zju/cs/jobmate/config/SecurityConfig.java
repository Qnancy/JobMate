package cn.edu.zju.cs.jobmate.config;

import cn.edu.zju.cs.jobmate.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;
    
    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;
    
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 启用CSRF保护
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            
            // 配置授权规则
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/register", "/api/auth/csrf").permitAll() // 允许注册和CSRF token获取
                .anyRequest().authenticated() // 其他所有请求需要认证
            )
            
            // 启用表单登录
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login") // 登录处理URL
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler) // 自定义成功处理器
                .failureHandler(failureHandler) // 自定义失败处理器
                .permitAll()
            )
            
            // 配置未认证处理
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
            )
            
            // 启用HTTP Basic认证
            .httpBasic(basic -> basic
                .authenticationEntryPoint(authenticationEntryPoint)
            );
        
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }
}