package model;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class ShortLink implements Serializable {
    private final String shortCode;
    private final String originalURL;
    private final UUID userId;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final Integer maxClicks;

    private int clicksCount;
    private boolean isActive;


    public ShortLink(String shortCode, String originalURL, UUID userId, Instant createdAt, Instant expiresAt, Integer maxClicks, int count, boolean isActive) {

        this.shortCode = shortCode;
        this.originalURL = originalURL;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.maxClicks = maxClicks;
        this.clicksCount = count;
        this.isActive = isActive;
    }

    //region Setters
    public void setActive(boolean active) {
        isActive = active;
    }

    public void setClicksCount(int clicksCount) {
        this.clicksCount = clicksCount;
    }

    //endregion
    //region Getters
    public Instant getExpiresAt() {
        return expiresAt;
    }

    public int getClicksCount() {
        return clicksCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public Integer getMaxClicks() {
        return maxClicks;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getOriginalUrl() {
        return originalURL;
    }
    //endregion
}