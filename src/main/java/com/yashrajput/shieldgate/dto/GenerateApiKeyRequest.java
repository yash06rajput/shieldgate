package com.yashrajput.shieldgate.dto;

public class GenerateApiKeyRequest {

    private String email;
    private String name;
    private int dailyQuota;

    public GenerateApiKeyRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(int dailyQuota) {
        this.dailyQuota = dailyQuota;
    }
}