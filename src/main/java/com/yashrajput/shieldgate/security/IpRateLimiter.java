package com.yashrajput.shieldgate.security;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class IpRateLimiter {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_MINUTES = 15;

    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final Map<String, LocalDateTime> blockedIps = new HashMap<>();

    public boolean isBlocked(String ip) {
        if (!blockedIps.containsKey(ip)) {
            return false;
        }

        LocalDateTime blockedUntil = blockedIps.get(ip);

        if (blockedUntil.isBefore(LocalDateTime.now())) {
            blockedIps.remove(ip);
            failedAttempts.remove(ip);
            return false;
        }

        return true;
    }

    public void recordFailure(String ip) {
        int attempts = failedAttempts.getOrDefault(ip, 0) + 1;
        failedAttempts.put(ip, attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            blockedIps.put(ip, LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES));
        }
    }

    public void clearFailures(String ip) {
        failedAttempts.remove(ip);
    }
}