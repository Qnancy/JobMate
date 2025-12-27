package cn.edu.zju.cs.jobmate.configs.security.handlers;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.utils.security.SecurityResponder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication entry point to handle
 * unauthorized access attempts in Spring Security filter chain.
 */
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final SecurityResponder responder;

    public AuthEntryPoint(SecurityResponder responder) {
        this.responder = responder;
    }

    @Override
    public void commence (
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
        responder.writeResponse(response, ApiResponse.error(ErrorCode.AUTHENTICATION_FAILED));
    }
}
