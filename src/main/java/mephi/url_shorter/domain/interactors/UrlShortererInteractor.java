package mephi.url_shorter.domain.interactors;

import java.util.Optional;
import java.util.UUID;

import mephi.url_shorter.domain.repositories.UrlShortererRepository;
import mephi.url_shorter.presenters.ShortenedUrlResponse;

public class UrlShortererInteractor {
    private final UrlShortererRepository repo;

    private final int MAX_REDIRECTS;

    public UrlShortererInteractor(UrlShortererRepository repository, int maxRedirects) {
        this.repo = repository;
        this.MAX_REDIRECTS = maxRedirects;
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
        int maxRedirects = repo.getMaxRedirects(shortUrl);
        if (maxRedirects < MAX_REDIRECTS) {
            maxRedirects = MAX_REDIRECTS;
        }
        if (counter >= maxRedirects) {
            repo.deleteShortUrl(shortUrl);
            repo.deleteRedirectCount(shortUrl);
            return Optional.ofNullable(null);
        }
        repo.incrementRedirectCount(shortUrl);
        return Optional.of(url);

    }

    public void deleteShortUrl(String shortUrl, UUID userId) throws PermissionDeniedException {
        String url = repo.getInitialUrl(shortUrl);
        if (url == null) {
            return;
        }
        if (!getShortUrl(url, userId).getShortenedUrl().equals(shortUrl)) {
            throw new PermissionDeniedException("Невозможно удалить чужую ссылку");
        }
        repo.deleteShortUrl(shortUrl);
        repo.deleteRedirectCount(shortUrl);
    }

    public void updateShortUrl(String shortUrl, UUID userId, int maxRedirects) throws PermissionDeniedException {
        String url = repo.getInitialUrl(shortUrl);
        if (url == null) {
            return;
        }
        if (!getShortUrl(url, userId).getShortenedUrl().equals(shortUrl)) {
            throw new PermissionDeniedException(
                    "Невозможно изменить максимальное колиечство переходов по чужой ссылке");
        }
        repo.setMaxRedirects(shortUrl, maxRedirects);
    }
}
