package com.yashrajput.shieldgate.repository;

import com.yashrajput.shieldgate.entity.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {

    long countByEventType(String eventType);

    long countBySeverity(String severity);

    List<SecurityEvent> findTop10ByOrderByCreatedAtDesc();
}