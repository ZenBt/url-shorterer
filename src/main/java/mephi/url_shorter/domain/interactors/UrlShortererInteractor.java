package mephi.url_shorter.domain.interactors;

import java.util.Optional;
import java.util.UUID;

import mephi.url_shorter.domain.repositories.UrlShortererRepository;
import mephi.url_shorter.presenters.ShortenedUrlResponse;

public class UrlShortererInteractor {
    private final UrlShortererRepository repo;

    private static final int MAX_REDIRECTS = 2;

    public UrlShortererInteractor(UrlShortererRepository repository) {
        this.repo = repository;
    }

    public ShortenedUrlResponse getShortUrl(String url, UUID userId) {
        String toShortenUrl = url + userId.toString();
        String shortenedUrl = Integer.toHexString(toShortenUrl.hashCode());

        repo.saveShortUrl(url, shortenedUrl);
        repo.setRedirectCount(shortenedUrl, 0);

        return new ShortenedUrlResponse(userId, shortenedUrl);
    }

    public ShortenedUrlResponse getShortUrl(String url) {
        UUID userId = UUID.randomUUID();

        return getShortUrl(url, userId);
    }

    public Optional<String> getInitialUrl(String shortUrl) {
        String url = repo.getInitialUrl(shortUrl);
        if (url == null) {
            return Optional.ofNullable(null);
        }
        int counter = repo.getRedirectCount(shortUrl);
        if (counter == 0) {
            repo.setRedirectCount(shortUrl, 1);
            return Optional.of(url);
        }
        if (counter > MAX_REDIRECTS) {
            repo.deleteShortUrl(shortUrl);
            repo.deleteRedirectCount(shortUrl);
            return Optional.ofNullable(null);
        }
        repo.incrementRedirectCount(shortUrl);
        return Optional.of(url);

    }
}
