package com.yashrajput.shieldgate.security;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class BurstDetector {

    private static final int MAX_REQUESTS = 10;
    private static final int WINDOW_SECONDS = 30;

    private final Map<String, List<LocalDateTime>> requestMap = new HashMap<>();

    public boolean isSuspicious(String apiKey) {
        LocalDateTime now = LocalDateTime.now();

        requestMap.putIfAbsent(apiKey, new ArrayList<>());

        List<LocalDateTime> requests = requestMap.get(apiKey);

        requests.removeIf(
                time -> time.isBefore(now.minusSeconds(WINDOW_SECONDS))
        );

        requests.add(now);

        return requests.size() >= MAX_REQUESTS;
    }
}