package cn.edu.zju.cs.jobmate.configs.security.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Base security handler used in Spring Security.
 */
@Slf4j
public abstract class BaseHandler {

    private final ObjectMapper mapper;

    public BaseHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Write response with given {@link ErrorCode}.
     * 
     * @param response the HttpServletResponse to write to
     */
    public void writeResponse(HttpServletResponse response, ErrorCode errorCode) {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
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
