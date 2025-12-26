package cn.edu.zju.cs.jobmate.utils.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Security responder used in Spring Security.
 */
@Slf4j
public abstract class SecurityResponder {

    private final ObjectMapper mapper;

    public SecurityResponder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Write response with given {@link ErrorCode}.
     * 
     * @param response the HttpServletResponse to write to
     * @param errorCode the ErrorCode
     */
    public void writeResponse(HttpServletResponse response, ErrorCode errorCode) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        Map<String, Object> body = new HashMap<>();
        body.put("code", errorCode.getHttpStatus().value());
        body.put("message", errorCode.getMessage());
        body.put("data", null);
        try {
            mapper.writeValue(response.getOutputStream(), body);
        } catch (IOException e) {
            log.error("Failed to write response: {}", e.getMessage(), e);
        }
    }
}
