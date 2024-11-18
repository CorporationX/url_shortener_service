package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    @Value("${spring.data.redis.duration:1}")
    private int duration;

    private final RedisTemplate<String, String> redisUrlTemplate;

    public void saveHash(Url url) {
        redisUrlTemplate.opsForValue().set(url.getHash(), url.getUrl(), duration, TimeUnit.DAYS);
    }

    public String getUrl(String hash) {
        return redisUrlTemplate.opsForValue().get(hash);
    }
}
