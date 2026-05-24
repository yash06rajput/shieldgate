package com.yashrajput.shieldgate.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_value", unique = true, nullable = false)
    private String keyValue;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private int dailyQuota = 1000;

    @Column(nullable = false)
    private int requestsUsed = 0;

    @Column(nullable = false)
    private LocalDate quotaResetDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // analytics / security fields
    @Column
    private LocalDateTime lastUsedAt;

    @Column(nullable = false)
    private int failedAttempts = 0;

    @Column
    private LocalDateTime lastFailedAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ApiKey() {
    }

    // ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // KEY VALUE
    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    // NAME
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ACTIVE
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // DAILY QUOTA
    public int getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(int dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    // REQUESTS USED
    public int getRequestsUsed() {
        return requestsUsed;
    }

    public void setRequestsUsed(int requestsUsed) {
        this.requestsUsed = requestsUsed;
    }

    // QUOTA RESET
    public LocalDate getQuotaResetDate() {
        return quotaResetDate;
    }

    public void setQuotaResetDate(LocalDate quotaResetDate) {
        this.quotaResetDate = quotaResetDate;
    }

    // CREATED AT
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // LAST USED
    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    // FAILED ATTEMPTS
    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    // LAST FAILED ATTEMPT
    public LocalDateTime getLastFailedAttempt() {
        return lastFailedAttempt;
    }

    public void setLastFailedAttempt(LocalDateTime lastFailedAttempt) {
        this.lastFailedAttempt = lastFailedAttempt;
    }

    // USER
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}