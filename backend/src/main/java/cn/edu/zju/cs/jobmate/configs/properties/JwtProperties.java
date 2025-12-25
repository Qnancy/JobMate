package cn.edu.zju.cs.jobmate.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * JWT related properties.
 * 
 * TODO: use environment variables to configure these properties for better security.
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * JWT secret key.
     */
    @NotBlank
    @Size(min = 32)
    private String secret;

    /**
     * JWT expiration time in milliseconds.
     */
    @NotNull
    @Positive
    private Long expiration;
}
