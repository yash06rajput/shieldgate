package com.yashrajput.shieldgate.controller;

import com.yashrajput.shieldgate.dto.SecurityStatsResponse;
import com.yashrajput.shieldgate.entity.SecurityEvent;
import com.yashrajput.shieldgate.service.SecurityEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityAnalyticsController {

    private final SecurityEventService securityEventService;

    public SecurityAnalyticsController(SecurityEventService securityEventService) {
        this.securityEventService = securityEventService;
    }

    @GetMapping("/stats")
    public SecurityStatsResponse getStats() {
        return securityEventService.getStats();
    }

    @GetMapping("/events")
    public List<SecurityEvent> getRecentEvents() {
        return securityEventService.getRecentEvents();
    }
}