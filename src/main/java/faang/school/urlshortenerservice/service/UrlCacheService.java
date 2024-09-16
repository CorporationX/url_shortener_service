package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class UrlCacheService {

    private final Jedis jedis;
    private final int ttl;

    public UrlCacheService(Jedis jedis,
                           @Value("${spring.cache.ttl:3600}") int ttl) {
        this.jedis = jedis;
        this.ttl = ttl;
    }

    public String getCachedLongUrl(String shortUrl) {
        return jedis.get(shortUrl);
    }

    public void cacheLongUrl(String shortUrl, String longUrl) {
        jedis.set(shortUrl, longUrl);
        jedis.expire(shortUrl, ttl);
    }

}