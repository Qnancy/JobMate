package cn.edu.zju.cs.jobmate.configs.security.handlers;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication entry point to handle
 * unauthorized access attempts in Spring Security filter chain.
 */
@Component
public class AuthEntryPoint extends BaseHandler implements AuthenticationEntryPoint {

    public AuthEntryPoint(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void commence (
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
        writeResponse(response, ErrorCode.AUTHENTICATION_FAILED);
    }
}
