package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCacheService {
    private final StringRedisTemplate redisTemplate;

//    public void saveByTtlInHour(Url url, long ttlInHour) {
//        try {
//            redisTemplate.opsForValue().set(url.getHash(), url.toString(), Duration.ofHours(ttlInHour));
//        } catch (JedisConnectionException e) {
//            log.error("Failed to save key {} with TTL {} in hours. Error: {}",
//                    url.getHash(), ttlInHour, e.getMessage(), e
//            );
//        }
//    }
//
//    public Optional<String> findByHash(String hash) {
//        try {
//            return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
//        } catch (JedisConnectionException e) {
//            log.error("Failed to fetch value for key {}. Error: {}",
//                    hash, e.getMessage(), e
//            );
//            return Optional.empty();
//        }
//    }

    public void deleteAllHashes(List<Hash> hashes) {
        redisTemplate.delete(hashes.toString());
    }
}
