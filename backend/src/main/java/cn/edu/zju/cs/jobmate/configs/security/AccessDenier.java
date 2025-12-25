package cn.edu.zju.cs.jobmate.configs.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.utils.security.ResponseWriterUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom access denied handler for handling
 * denied access attempts in Spring Security filter chain.
 */
@Component
public class AccessDenier implements AccessDeniedHandler {

    private final ResponseWriterUtil writer;

    public AccessDenier(ResponseWriterUtil writer) {
        this.writer = writer;
    }

    @Override
    public void handle (
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        writer.writeResponse(response, ErrorCode.FORBIDDEN_ACCESS);
    }
}
