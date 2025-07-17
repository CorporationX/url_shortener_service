package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisHashCache {

    private final RedisTemplate<String, Hash> redisTemplate;

    private final String KEY = "free_hashes";

    public void addHashes(List<Hash> hashes) {
        redisTemplate.opsForList().rightPushAll(KEY, hashes);
    }

    public Hash pollHash() {
        return redisTemplate.opsForList().leftPop(KEY);
    }

    public Long cacheSize() {
        return redisTemplate.opsForList().size(KEY);
    }
}
