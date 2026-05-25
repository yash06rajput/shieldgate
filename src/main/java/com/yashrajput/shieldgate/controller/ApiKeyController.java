package com.yashrajput.shieldgate.controller;

import com.yashrajput.shieldgate.dto.ApiKeyResponse;
import com.yashrajput.shieldgate.dto.GenerateApiKeyRequest;
import com.yashrajput.shieldgate.entity.ApiKey;
import com.yashrajput.shieldgate.service.ApiKeyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/generate")
    public ApiKeyResponse generateKey(@RequestBody GenerateApiKeyRequest request) {
        ApiKey apiKey = apiKeyService.generateApiKey(
                request.getEmail(),
                request.getName(),
                request.getDailyQuota()
        );

        return mapToResponse(apiKey);
    }

    @GetMapping("/my")
    public List<ApiKeyResponse> getMyKeys(@RequestParam String email) {
        return apiKeyService.getKeysByEmail(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}/toggle")
    public ApiKeyResponse toggleKey(@PathVariable Long id) {
        return mapToResponse(apiKeyService.toggleKey(id));
    }
    @DeleteMapping("/{id}")
public Map<String, String> deleteKey(@PathVariable Long id) {
    apiKeyService.deleteKey(id);
    return Map.of("message", "API key deleted successfully");
}

    // DASHBOARD ENDPOINTS

    @GetMapping("/dashboard/overview")
    public Map<String, Object> dashboardOverview() {
        return apiKeyService.getDashboardOverview();
    }

    @GetMapping("/dashboard/top-keys")
    public List<ApiKeyResponse> topKeys() {
        return apiKeyService.getTopKeys()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ApiKeyResponse mapToResponse(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getKeyValue(),
                apiKey.getName(),
                apiKey.isActive(),
                apiKey.getDailyQuota(),
                apiKey.getRequestsUsed()
        );
    }
}