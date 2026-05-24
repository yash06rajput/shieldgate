package com.yashrajput.shieldgate.service;

import com.yashrajput.shieldgate.dto.SecurityStatsResponse;
import com.yashrajput.shieldgate.entity.SecurityEvent;
import com.yashrajput.shieldgate.repository.SecurityEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityEventService {

    private final SecurityEventRepository repository;

    public SecurityEventService(SecurityEventRepository repository) {
        this.repository = repository;
    }

    public void logEvent(
            String eventType,
            String severity,
            String ipAddress,
            String endpoint,
            String details
    ) {
        SecurityEvent event = new SecurityEvent();

        event.setEventType(eventType);
        event.setSeverity(severity);
        event.setIpAddress(ipAddress);
        event.setEndpoint(endpoint);
        event.setMessage(details);

        repository.save(event);
    }

    public SecurityStatsResponse getStats() {
        return new SecurityStatsResponse(
                repository.count(),
                repository.countBySeverity("CRITICAL"),
                repository.countBySeverity("HIGH"),
                repository.countBySeverity("MEDIUM")
        );
    }

    public List<SecurityEvent> getRecentEvents() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }
}