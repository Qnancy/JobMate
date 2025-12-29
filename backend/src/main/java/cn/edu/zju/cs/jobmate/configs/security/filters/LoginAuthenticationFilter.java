package cn.edu.zju.cs.jobmate.configs.security.filters;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;

import cn.edu.zju.cs.jobmate.configs.security.jwt.JwtTokenProvider;
import cn.edu.zju.cs.jobmate.dto.authentication.LoginRequest;
import cn.edu.zju.cs.jobmate.dto.authentication.LoginResponse;
import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.utils.security.SecurityResponder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Login authentication filter for handling login requests with JSON payloads.
 */
@Slf4j
public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final SecurityResponder responder;
    private final ObjectMapper mapper;
    private final JwtTokenProvider tokenProvider;

    public LoginAuthenticationFilter(
        AuthenticationManager authenticationManager,
        SecurityResponder responder,
        ObjectMapper mapper,
        JwtTokenProvider tokenProvider
    ) {
        super(authenticationManager);
        this.responder = responder;
        this.mapper = mapper;
        this.tokenProvider = tokenProvider;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginRequest loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);
            log.info("Attempting authentication: {}", loginRequest);
            UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                );
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new AuthenticationException("Failed to authenticate login request", e) {};
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult
    ) throws IOException, ServletException {
        // Get user details.
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();

        // Generate JWT token.
        try {
            String token = tokenProvider.generateToken(userDetails);
            LoginResponse body = LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .build();
            responder.writeResponse(response, ApiResponse.ok("登录成功", body));
            log.info("User '{}' logined successfully", userDetails.getUsername());
        } catch (JOSEException e) {
            log.error("Failed to generate JWT token: {}", e.getMessage());
            responder.writeResponse(response, ApiResponse.error(ErrorCode.TOKEN_SIGNING_ERROR));
        }
    }

    @Override
    protected void unsuccessfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException failed
    ) throws IOException, ServletException {
        ErrorCode error = switch (failed) {
            case BadCredentialsException ex -> {
                log.info("Authentication failed: {}", failed.getMessage());
                yield ErrorCode.INVALID_AUTHENTICATION;
            }
            default -> {
                log.error("Authentication failed: {}", failed.getMessage());
                yield ErrorCode.AUTHENTICATION_FAILED;
            }
        };
        responder.writeResponse(response, ApiResponse.error(error));
    }
}
