package mephi.url_shorter.domain.repositories;

public abstract class UrlShortererRepository {
    public abstract void saveShortUrl(String initialUrl, String shortUrl);

    public abstract String getInitialUrl(String shortUrl);

    public abstract int getRedirectCount(String shortUrl);

    public abstract void deleteRedirectCount(String shortUrl);

    public abstract void deleteShortUrl(String shortUrl);

    public abstract void incrementRedirectCount(String shortUrl);

    public abstract void setRedirectCount(String shortUrl, int counter);
}
