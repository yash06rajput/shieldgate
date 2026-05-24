package com.yashrajput.shieldgate.dto;

public class SecurityStatsResponse {

    private long totalEvents;
    private long criticalEvents;
    private long highEvents;
    private long mediumEvents;

    public SecurityStatsResponse(
            long totalEvents,
            long criticalEvents,
            long highEvents,
            long mediumEvents
    ) {
        this.totalEvents = totalEvents;
        this.criticalEvents = criticalEvents;
        this.highEvents = highEvents;
        this.mediumEvents = mediumEvents;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public long getCriticalEvents() {
        return criticalEvents;
    }

    public long getHighEvents() {
        return highEvents;
    }

    public long getMediumEvents() {
        return mediumEvents;
    }
}