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

    private final ApiKeyRepository apiKeyRepository;
    private final SecurityEventService securityEventService;

    public ApiKeyFilter(
            ApiKeyRepository apiKeyRepository,
            SecurityEventService securityEventService
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.securityEventService = securityEventService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = getClientIp(request);

        // Protect only vendor APIs
        if (!path.startsWith("/api/vendor")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKeyValue = request.getHeader("x-api-key");

        // Missing API Key
        if (apiKeyValue == null || apiKeyValue.isBlank()) {

            securityEventService.logEvent(
                    "MISSING_API_KEY",
                    "HIGH",
                    ip,
                    path,
                    "Request without API key"
            );

            logger.warn(
                    "SECURITY_EVENT=missing_api_key IP={} PATH={}",
                    ip,
                    path
            );

            response.sendError(401, "Missing API Key");
            return;
        }

        ApiKey apiKey = apiKeyRepository
                .findByKeyValue(apiKeyValue.trim())
                .orElse(null);

        // Invalid API Key
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

            response.sendError(401, "Invalid API Key");
            return;
        }

        // Disabled API Key
        if (!apiKey.isActive()) {

            securityEventService.logEvent(
                    "DISABLED_KEY_USED",
                    "CRITICAL",
                    ip,
                    path,
                    "Disabled API key attempted"
            );

            logger.warn(
                    "SECURITY_EVENT=disabled_key_used IP={} PATH={} KEY_NAME={}",
                    ip,
                    path,
                    apiKey.getName()
            );

            response.sendError(403, "API Key Disabled");
            return;
        }

        // Reset daily quota if new day
        if (apiKey.getQuotaResetDate() == null ||
                !LocalDate.now().equals(apiKey.getQuotaResetDate())) {

            apiKey.setRequestsUsed(0);
            apiKey.setQuotaResetDate(LocalDate.now());
        }

        // Quota exceeded
        if (apiKey.getRequestsUsed() >= apiKey.getDailyQuota()) {

            securityEventService.logEvent(
                    "QUOTA_EXCEEDED",
                    "MEDIUM",
                    ip,
                    path,
                    "API quota exceeded"
            );

            logger.warn(
                    "SECURITY_EVENT=quota_exceeded IP={} KEY_NAME={} USED={}/{}",
                    ip,
                    apiKey.getName(),
                    apiKey.getRequestsUsed(),
                    apiKey.getDailyQuota()
            );

            response.sendError(429, "Daily quota exceeded");
            return;
        }

        // Usage analytics
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