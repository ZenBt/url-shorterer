package mephi.url_shorter.presenters;

import java.util.UUID;

public class ShortenedUrlResponse {
    private UUID userId;
    private String shortenedUrl;

    public ShortenedUrlResponse(UUID userId, String shortenedUrl) {
        this.userId = userId;
        this.shortenedUrl = shortenedUrl;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getShortenedUrl() {
        return shortenedUrl;
    }

    public void setShortenedUrl(String shortenedUrl) {
        this.shortenedUrl = shortenedUrl;
    }
}