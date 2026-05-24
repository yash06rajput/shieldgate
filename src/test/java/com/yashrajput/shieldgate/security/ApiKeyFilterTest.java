package com.yashrajput.shieldgate.security;

import com.yashrajput.shieldgate.repository.ApiKeyRepository;
import com.yashrajput.shieldgate.service.SecurityEventService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.mockito.Mockito.*;

class ApiKeyFilterTest {

    @Test
    void shouldRejectInvalidApiKey() throws Exception {

        ApiKeyRepository repository = mock(ApiKeyRepository.class);
        SecurityEventService securityEventService = mock(SecurityEventService.class);

        ApiKeyFilter filter = new ApiKeyFilter(
                repository,
                securityEventService
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/vendor/test");
        request.addHeader("x-api-key", "fake_key");

        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(repository.findByKeyValue("fake_key"))
                .thenReturn(Optional.empty());

        filter.doFilter(request, response, chain);

        verify(response).sendError(401, "Invalid API Key");

        verify(securityEventService).logEvent(
                eq("INVALID_API_KEY"),
                eq("CRITICAL"),
                anyString(),
                eq("/api/vendor/test"),
                eq("Unknown API key attempted")
        );
    }
}