package com.yashrajput.shieldgate.dto;

public class ApiKeyResponse {

    private Long id;
    private String keyValue;
    private String name;
    private boolean active;
    private int dailyQuota;
    private int requestsUsed;

    public ApiKeyResponse() {
    }

    public ApiKeyResponse(Long id, String keyValue, String name,
                          boolean active, int dailyQuota, int requestsUsed) {
        this.id = id;
        this.keyValue = keyValue;
        this.name = name;
        this.active = active;
        this.dailyQuota = dailyQuota;
        this.requestsUsed = requestsUsed;
    }

    public Long getId() {
        return id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public int getDailyQuota() {
        return dailyQuota;
    }

    public int getRequestsUsed() {
        return requestsUsed;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDailyQuota(int dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public void setRequestsUsed(int requestsUsed) {
        this.requestsUsed = requestsUsed;
    }
}