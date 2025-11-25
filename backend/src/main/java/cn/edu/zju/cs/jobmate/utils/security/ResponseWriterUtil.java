package cn.edu.zju.cs.jobmate.utils.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Response writer utility class for Spring Security.
 * 
 * @apiNote Only used in Spring Security.
 */
public class ResponseWriterUtil {

    /**
     * Object mapper for JSON serialization.
     * 
     * @apiNote Thread-safe singleton.
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Write response with given status code and message.
     * 
     * @param response HttpServletResponse
     * @param code HTTP status code
     * @param message Response message
     */
    public static void writeResponse(HttpServletResponse response, int code, String message) {
        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        try {
            mapper.writeValue(response.getOutputStream(), body);
        } catch (IOException e) {
            // TODO: log error here.
        }
    }
}
