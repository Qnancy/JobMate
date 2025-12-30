package cn.edu.zju.cs.jobmate.configs.security.handlers;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.utils.httpservlet.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom access denied handler for handling
 * denied access attempts in Spring Security filter chain.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessDenier implements AccessDeniedHandler {

    private final ResponseUtil responder;

    @Override
    public void handle (
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        log.info("Access denied: {}", accessDeniedException.getMessage());
        responder.writeResponse(response, ApiResponse.error(ErrorCode.FORBIDDEN_ACCESS));
    }
}
