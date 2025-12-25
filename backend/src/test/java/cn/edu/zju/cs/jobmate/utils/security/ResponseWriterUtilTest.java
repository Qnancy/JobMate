package cn.edu.zju.cs.jobmate.utils.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResponseWriterUtilTest {

    @Test
    void testWriteResponse() throws IOException {
        // Arrange
        HttpServletResponse response = mock(HttpServletResponse.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                baos.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // no-op for test
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        ObjectMapper mapper = new ObjectMapper();
        ResponseWriterUtil util = new ResponseWriterUtil(mapper);

        // Act
        util.writeResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);

        // Assert
        verify(response).setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value());
        verify(response).setContentType("application/json");

        var map = mapper.readValue(baos.toByteArray(), Map.class);

        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value(), map.get("code"));
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), map.get("message"));
        assertNull(map.get("data"));
    }
}
