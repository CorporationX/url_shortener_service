package faang.school.urlshortenerservice.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String,String> redisTemplate;

    @Value("${service.redis.ttlInSeconds}")
    private long ttlInSeconds;

    public void save(String hash, String url){
        redisTemplate.opsForValue().set(hash, url, ttlInSeconds, TimeUnit.SECONDS);
    }

    public Optional<String> findByHash (String hash){
        String value = redisTemplate.opsForValue().get(hash);

        if(value != null){
            redisTemplate.expire(hash, ttlInSeconds, TimeUnit.SECONDS);
        }

        return Optional.ofNullable(value);
    }
}
