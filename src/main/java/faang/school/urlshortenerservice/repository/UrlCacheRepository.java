package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(Url url) {
        redisTemplate.opsForHash().put("URL", url.getHash(), url.getUrl());
    }

    public String findByHash(String hash) {
        return (String) redisTemplate.opsForHash().get("URL", hash);
    }

    public void deleteHashes(List<String> hashes) {
        redisTemplate.opsForHash().delete("URL", hashes);
    }
}