package cn.edu.zju.cs.jobmate.utils.httpservlet;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HttpServletResponse} related utilities.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseUtil {

    private final ObjectMapper mapper;

    /**
     * Write {@link ApiResponse} to HttpServletResponse.
     * 
     * @param response the HttpServletResponse to write to
     * @param apiResponse the API response to write
     */
    public void writeResponse(HttpServletResponse response, ApiResponse<?> apiResponse) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(apiResponse.getCode());
        try {
            mapper.writeValue(response.getOutputStream(), apiResponse);
        } catch (IOException e) {
            log.error("Failed to write response: {}", e.getMessage());
        }
    }
}
