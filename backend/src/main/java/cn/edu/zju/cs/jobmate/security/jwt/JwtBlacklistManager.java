package cn.edu.zju.cs.jobmate.security.jwt;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT blacklist manager.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtBlacklistManager {

    private static final String PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redis;
    private final JwtTokenProvider provider;

    /**
     * Add a token to the blacklist.
     * 
     * @param token the JWT token to be blacklisted
     */
    public void addToBlacklist(String token) {
        try {
            long remain = provider.getRemainTimeFromToken(token) / 1000;
            if (remain <= 0) {
                return ;
            }
            redis.opsForValue().set(PREFIX + token, "BL", remain, TimeUnit.SECONDS);
        } catch (ParseException e) {
            log.error("Unexpected invalid token (should not happen): {}", token);
            // No need to throw exception here because 
            // the token is already validated in JwtAuthenticationFilter.
        }
    }

    /**
     * Check if a token is blacklisted.
     * 
     * @param token the JWT token to be checked
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + token));
    }
}
