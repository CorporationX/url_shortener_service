package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveInCache(Url url) {
        try {
            redisTemplate.opsForValue().set(url.getHash(), url);
        } catch (JedisConnectionException e) {
            log.error("Failed to save key {}. Error: {}",
                    url.getHash(), e.getMessage(), e
            );
        }
    }

    public Optional<Object> findByHash(String hash) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
        } catch (JedisConnectionException e) {
            log.error("Failed to fetch value for key {}. Error: {}",
                    hash, e.getMessage(), e
            );
            return Optional.empty();
        }
    }

    public void deleteAllHashes(List<Hash> hashes) {
        redisTemplate.delete(hashes.toString());
    }
}
