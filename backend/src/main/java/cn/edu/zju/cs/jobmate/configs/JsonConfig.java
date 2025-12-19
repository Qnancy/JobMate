package cn.edu.zju.cs.jobmate.configs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Jackson JSON configurations.
 */
@Configuration
public class JsonConfig {

    /**
     * DateTime format used in JSON serialization/deserialization.
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Set naming strategy to snake_case.
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        // Register LocalDateTime formatter.
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(
            LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT))
        );
        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
