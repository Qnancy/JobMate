package cn.edu.zju.cs.jobmate.configs.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * OpenAI-compatible LLM settings for admin promotion parsing.
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app.llm")
public class LlmProperties {

    /**
     * When false, promotion-parse API returns not-configured without calling upstream.
     */
    private boolean enabled = true;

    /**
     * API key; prefer env {@code OPENAI_API_KEY} via YAML default.
     */
    private String apiKey = "";

    /**
     * Base URL without trailing slash, e.g. https://api.openai.com/v1
     */
    private String baseUrl = "https://api.openai.com/v1";

    /**
     * Chat completions model name.
     */
    private String model = "gpt-4o-mini";

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }
}
