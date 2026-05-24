package com.yashrajput.shieldgate.service;

import com.yashrajput.shieldgate.entity.ApiKey;
import com.yashrajput.shieldgate.entity.User;
import com.yashrajput.shieldgate.repository.ApiKeyRepository;
import com.yashrajput.shieldgate.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    void shouldGenerateApiKeySuccessfully() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(apiKeyRepository.save(any(ApiKey.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApiKey apiKey = apiKeyService.generateApiKey(
                "test@example.com",
                "Demo Key",
                100
        );

        assertNotNull(apiKey);
        assertEquals("Demo Key", apiKey.getName());
        assertEquals(100, apiKey.getDailyQuota());
        assertTrue(apiKey.isActive());

        verify(apiKeyRepository, times(1)).save(any(ApiKey.class));
    }
}