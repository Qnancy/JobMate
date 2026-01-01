package cn.edu.zju.cs.jobmate.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.edu.zju.cs.jobmate.utils.generators.SnowflakeIdGenerator;

/**
 * Generator configurations.
 */
@Configuration
public class GeneratorConfig {

    /**
     * Snowflake ID generator bean.
     * 
     * @return SnowflakeIdGenerator instance
     */
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator(0, 0);
    }
}
