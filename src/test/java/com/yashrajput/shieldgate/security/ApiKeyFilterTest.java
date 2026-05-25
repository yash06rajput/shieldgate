package com.yashrajput.shieldgate.security;

import com.yashrajput.shieldgate.repository.ApiKeyRepository;
import com.yashrajput.shieldgate.service.SecurityEventService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ApiKeyFilterTest {

    @Test
    void shouldRejectInvalidApiKey() throws Exception {
        ApiKeyRepository repository = mock(ApiKeyRepository.class);
        SecurityEventService securityEventService = mock(SecurityEventService.class);
        IpRateLimiter ipRateLimiter = new IpRateLimiter();
        BurstDetector burstDetector = new BurstDetector();
        RedisRateLimiter redisRateLimiter = mock(RedisRateLimiter.class);

        ApiKeyFilter filter = new ApiKeyFilter(
                repository,
                securityEventService,
                ipRateLimiter,
                burstDetector,
                redisRateLimiter
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/vendor/test");
        request.addHeader("x-api-key", "fake_key");
        request.setRemoteAddr("127.0.0.1");

        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(repository.findByKeyValue("fake_key"))
                .thenReturn(Optional.empty());

        when(redisRateLimiter.allowRequest(anyString(), anyInt(), anyInt()))
                .thenReturn(true);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(401), anyString());
    }
}