package com.yashrajput.shieldgate.security;

import com.yashrajput.shieldgate.entity.ApiKey;
import com.yashrajput.shieldgate.repository.ApiKeyRepository;
import com.yashrajput.shieldgate.service.SecurityEventService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCK_WINDOW_MINUTES = 15;

    private final ApiKeyRepository apiKeyRepository;
    private final SecurityEventService securityEventService;
    private final IpRateLimiter ipRateLimiter;
    private final BurstDetector burstDetector;
    private final RedisRateLimiter redisRateLimiter;

    public ApiKeyFilter(
            ApiKeyRepository apiKeyRepository,
            SecurityEventService securityEventService,
            IpRateLimiter ipRateLimiter,
            BurstDetector burstDetector,
            RedisRateLimiter redisRateLimiter
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.securityEventService = securityEventService;
        this.ipRateLimiter = ipRateLimiter;
        this.burstDetector = burstDetector;
        this.redisRateLimiter = redisRateLimiter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = getClientIp(request);

        if (!path.startsWith("/api/vendor")) {
            filterChain.doFilter(request, response);
            return;
        }

        // CLEAN LOCALHOST DISPLAY
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "LOCALHOST";
        }

        // BLOCKED IP CHECK
        if (ipRateLimiter.isBlocked(ip)) {
            securityEventService.logEvent(
                    "IP_BLOCKED",
                    "CRITICAL",
                    ip,
                    path,
                    "Blocked IP attempted access"
            );

            response.sendError(423, "Too many failed attempts. IP temporarily blocked.");
            return;
        }

        String apiKeyValue = request.getHeader("x-api-key");

        // MISSING KEY
        if (apiKeyValue == null || apiKeyValue.isBlank()) {
            securityEventService.logEvent(
                    "MISSING_API_KEY",
                    "HIGH",
                    ip,
                    path,
                    "Request without API key"
            );

            ipRateLimiter.recordFailure(ip);
            response.sendError(401, "Missing API Key");
            return;
        }

        ApiKey apiKey = apiKeyRepository
                .findByKeyValue(apiKeyValue.trim())
                .orElse(null);

        // INVALID KEY
        if (apiKey == null) {
            securityEventService.logEvent(
                    "INVALID_API_KEY",
                    "CRITICAL",
                    ip,
                    path,
                    "Unknown API key attempted"
            );

            logger.warn(
                    "SECURITY_EVENT=invalid_api_key IP={} PATH={} KEY={}",
                    ip,
                    path,
                    maskKey(apiKeyValue)
            );

            ipRateLimiter.recordFailure(ip);
            response.sendError(401, "Invalid API Key");
            return;
        }

        // LOCKED KEY
        if (apiKey.getFailedAttempts() >= MAX_FAILED_ATTEMPTS &&
                apiKey.getLastFailedAttempt() != null &&
                apiKey.getLastFailedAttempt()
                        .plusMinutes(LOCK_WINDOW_MINUTES)
                        .isAfter(LocalDateTime.now())) {

            securityEventService.logEvent(
                    "API_KEY_LOCKED",
                    "CRITICAL",
                    ip,
                    path,
                    "Locked API key attempted"
            );

            response.sendError(423, "API key temporarily locked");
            return;
        }

        // DISABLED KEY
        if (!apiKey.isActive()) {
            securityEventService.logEvent(
                    "DISABLED_KEY_USED",
                    "CRITICAL",
                    ip,
                    path,
                    "Disabled API key attempted"
            );

            response.sendError(403, "API Key Disabled");
            return;
        }

        // DAILY RESET
        if (apiKey.getQuotaResetDate() == null ||
                !LocalDate.now().equals(apiKey.getQuotaResetDate())) {

            apiKey.setRequestsUsed(0);
            apiKey.setQuotaResetDate(LocalDate.now());
        }

        // DAILY QUOTA CHECK
        if (apiKey.getRequestsUsed() >= apiKey.getDailyQuota()) {
            securityEventService.logEvent(
                    "QUOTA_EXHAUSTED",
                    "HIGH",
                    ip,
                    path,
                    "Daily quota exhausted"
            );

            response.sendError(429, "Daily quota exhausted");
            return;
        }

        // REDIS SHORT WINDOW RATE LIMIT
        boolean allowed;

        try {
            allowed = redisRateLimiter.allowRequest(
                    apiKeyValue,
                    5,   // max requests
                    10   // per 10 seconds
            );
        } catch (Exception e) {
            logger.warn("Redis unavailable, falling back to DB quota only");
            allowed = true;
        }

        if (!allowed) {
            securityEventService.logEvent(
                    "RATE_LIMIT_EXCEEDED",
                    "HIGH",
                    ip,
                    path,
                    "Redis short-window rate limit exceeded"
            );

            response.sendError(429, "Rate limit exceeded");
            return;
        }

        // BURST DETECTION
        if (burstDetector.isSuspicious(apiKey.getKeyValue())) {
            securityEventService.logEvent(
                    "SUSPICIOUS_BURST",
                    "CRITICAL",
                    ip,
                    path,
                    "Abnormally high request burst detected"
            );

            logger.warn(
                    "SECURITY_EVENT=burst_attack IP={} KEY_NAME={}",
                    ip,
                    apiKey.getName()
            );
        }

        // SUCCESS PATH
        ipRateLimiter.clearFailures(ip);

        apiKey.setFailedAttempts(0);
        apiKey.setLastFailedAttempt(null);

        apiKey.setRequestsUsed(apiKey.getRequestsUsed() + 1);
        apiKey.setLastUsedAt(LocalDateTime.now());

        apiKeyRepository.save(apiKey);

        logger.info(
                "ACCESS_GRANTED IP={} PATH={} KEY_NAME={} USED={}/{}",
                ip,
                path,
                apiKey.getName(),
                apiKey.getRequestsUsed(),
                apiKey.getDailyQuota()
        );

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private String maskKey(String key) {
        if (key == null || key.length() < 8) {
            return "****";
        }

        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }
}