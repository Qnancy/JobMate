package cn.edu.zju.cs.jobmate.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Admin related properties.
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app.admin")
public class AdminProperties {

    /**
     * Admin secret for admin account registration.
     */
    @NotNull
    private String secret;
}
