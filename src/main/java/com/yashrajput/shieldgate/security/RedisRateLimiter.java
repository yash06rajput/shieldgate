package com.yashrajput.shieldgate.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allowRequest(String apiKey, int limit, int windowSeconds) {
        String key = "rate_limit:" + apiKey;

        try {
            Long count = redisTemplate.opsForValue().increment(key);

            if (count == null) {
                return true; // fallback safety
            }

            if (count == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
            }

            return count <= limit;

        } catch (Exception e) {
            return true; // ApiKeyFilter fallback handles quota
        }
    }
}