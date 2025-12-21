package cn.edu.zju.cs.jobmate.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    JsonConfig.class,
    JacksonAutoConfiguration.class
})
class JsonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    static class Demo {
        private Integer companyId;
        private String title;
        private LocalDateTime createTime;

        public Demo(Integer companyId, String title, LocalDateTime createTime) {
            this.companyId = companyId;
            this.title = title;
            this.createTime = createTime;
        }

        public Integer getCompanyId() { return companyId; }
        public String getTitle() { return title; }
        public LocalDateTime getCreateTime() { return createTime; }
    }

    @Test
    void testJsonConfig() throws Exception {
        Demo demo = new Demo(
            123,
            "hello",
            LocalDateTime.of(2025, 12, 20, 15, 30, 45)
        );
        String json = objectMapper.writeValueAsString(demo);

        assertTrue(json.contains("\"company_id\":123"));
        assertTrue(json.contains("\"title\":\"hello\""));
        assertTrue(json.contains("\"create_time\":\"2025-12-20 15:30:45\""));
    }
}
