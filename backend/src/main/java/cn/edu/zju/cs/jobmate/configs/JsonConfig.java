package cn.edu.zju.cs.jobmate.configs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Jackson JSON configurations.
 */
@Configuration
public class JsonConfig {

    /**
     * DateTime format used in JSON serialization/deserialization.
     */
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    @SuppressWarnings("null")
    public Jackson2ObjectMapperBuilderCustomizer customizeObjectMapper() {
        return (Jackson2ObjectMapperBuilder builder) -> {
            // Set naming strategy to snake_case.
            builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            // Set LocalDateTime formatter.
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATETIME_FORMAT));
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMAT));
            // Set timezone to Asia/Shanghai.
            builder.timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            // Set ignore unknown properties.
            builder.failOnUnknownProperties(false);
            // Set empty beans serialization.
            builder.failOnEmptyBeans(false);
        };
    }
}
