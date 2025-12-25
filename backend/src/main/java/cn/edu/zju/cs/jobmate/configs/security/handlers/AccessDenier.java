package cn.edu.zju.cs.jobmate.configs.security.handlers;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom access denied handler for handling
 * denied access attempts in Spring Security filter chain.
 */
@Component
public class AccessDenier extends BaseHandler implements AccessDeniedHandler {

    public AccessDenier(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void handle (
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        writeResponse(response, ErrorCode.FORBIDDEN_ACCESS);
    }
}
