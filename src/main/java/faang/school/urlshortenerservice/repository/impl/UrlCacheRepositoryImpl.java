package faang.school.urlshortenerservice.repository.impl;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

  private static final String PREFIX = "short_url:";

  private final StringRedisTemplate redisTemplate;

  public void saveUrl(String hash, String longUrl) {
    redisTemplate.opsForValue().set(PREFIX + hash, longUrl, 1, TimeUnit.DAYS);
  }

  public Optional<String> getUrl(String hash) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(PREFIX + hash));
  }

}