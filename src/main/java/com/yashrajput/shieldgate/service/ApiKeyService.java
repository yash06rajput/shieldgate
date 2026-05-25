package com.yashrajput.shieldgate.service;

import com.yashrajput.shieldgate.entity.ApiKey;
import com.yashrajput.shieldgate.entity.User;
import com.yashrajput.shieldgate.repository.ApiKeyRepository;
import com.yashrajput.shieldgate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository,
                         UserRepository userRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.userRepository = userRepository;
    }

    public ApiKey generateApiKey(String email, String name, int dailyQuota) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ApiKey apiKey = new ApiKey();

        apiKey.setKeyValue("sk_live_" + UUID.randomUUID().toString().replace("-", ""));
        apiKey.setName(name);
        apiKey.setActive(true);
        apiKey.setDailyQuota(dailyQuota);
        apiKey.setRequestsUsed(0);
        apiKey.setQuotaResetDate(LocalDate.now());
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKey.setFailedAttempts(0);
        apiKey.setUser(user);

        return apiKeyRepository.save(apiKey);
    }

    public List<ApiKey> getKeysByEmail(String email) {
        return apiKeyRepository.findByUserEmail(email);
    }

    public ApiKey toggleKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API key not found"));

        apiKey.setActive(!apiKey.isActive());

        return apiKeyRepository.save(apiKey);
    }
    public void deleteKey(Long id) {
    ApiKey apiKey = apiKeyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("API key not found"));

    apiKeyRepository.delete(apiKey);
}

    // DASHBOARD METHODS

    public Map<String, Object> getDashboardOverview() {
        List<ApiKey> keys = apiKeyRepository.findAll();

        long totalKeys = keys.size();
        long activeKeys = apiKeyRepository.countByActive(true);
        long disabledKeys = totalKeys - activeKeys;

        int totalRequestsToday = keys.stream()
                .mapToInt(ApiKey::getRequestsUsed)
                .sum();

        int totalFailedAttempts = keys.stream()
                .mapToInt(ApiKey::getFailedAttempts)
                .sum();

        Map<String, Object> data = new HashMap<>();
        data.put("totalKeys", totalKeys);
        data.put("activeKeys", activeKeys);
        data.put("disabledKeys", disabledKeys);
        data.put("totalRequestsToday", totalRequestsToday);
        data.put("failedAttempts", totalFailedAttempts);

        return data;
    }

    public List<ApiKey> getTopKeys() {
        return apiKeyRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(ApiKey::getRequestsUsed).reversed())
                .limit(5)
                .toList();
    }
}