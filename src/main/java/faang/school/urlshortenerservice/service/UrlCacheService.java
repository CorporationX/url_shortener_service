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

  public String getCachedLongUrl(String hash) {
    return jedis.get(hash);
  }

  public void cacheLongUrl(String hash, String longUrl) {
    jedis.set(hash, longUrl);
    jedis.expire(hash, ttl);
  }
}