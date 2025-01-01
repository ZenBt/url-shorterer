package mephi.url_shorter.data.repositories;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import mephi.url_shorter.domain.repositories.UrlShortererRepository;

@Repository
public class RedisUrlShortererRepository extends UrlShortererRepository {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisUrlShortererRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String SHORT_URL_KEY = "short-url:";
    private static final String REDIRECT_COUNTER_KEY = "redirect-counter:";
    private static final int TTL_IN_DAYS = 10;

    @Override
    public void saveShortUrl(String initialUrl, String shortUrl) {
        Duration ttl = Duration.ofDays(TTL_IN_DAYS);
        redisTemplate.opsForValue().set(SHORT_URL_KEY + shortUrl, initialUrl, ttl);
    }

    @Override
    public String getInitialUrl(String shortUrl) {
        return redisTemplate.opsForValue().get(SHORT_URL_KEY + shortUrl);
    }

    @Override
    public int getRedirectCount(String shortUrl) {
        String count = redisTemplate.opsForValue().get(REDIRECT_COUNTER_KEY + shortUrl);
        if (count != null) {
            return Integer.parseInt(count);
        }
        return 0;
    }

    @Override
    public void deleteRedirectCount(String shortUrl) {
        redisTemplate.opsForValue().getOperations().delete(REDIRECT_COUNTER_KEY + shortUrl);
    }

    @Override
    public void deleteShortUrl(String shortUrl) {
        redisTemplate.opsForValue().getOperations().delete(SHORT_URL_KEY + shortUrl);
    }

    @Override
    public void incrementRedirectCount(String shortUrl) {
        redisTemplate.opsForValue().increment(REDIRECT_COUNTER_KEY + shortUrl);
    }

    @Override
    public void setRedirectCount(String shortUrl, int counter) {
        Duration ttl = Duration.ofDays(TTL_IN_DAYS);
        redisTemplate.opsForValue().set(REDIRECT_COUNTER_KEY + shortUrl, String.valueOf(counter), ttl);
    }
}