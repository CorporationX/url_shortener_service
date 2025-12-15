package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
	private final RedisTemplate<String, String> redisTemplate;

	@Value("${app.redis.url-cache-ttl-hours:24}")
	private int urlCacheTtlHours;

	public void saveUrlToRedis(String hash, String longUrl) {
		redisTemplate.opsForValue().set(hash, longUrl, Duration.ofHours(urlCacheTtlHours));
	}

	public String findUrlByHash(String hash) {
		return redisTemplate.opsForValue().get(hash);
	}
}
