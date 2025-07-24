package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.redis.hash_cache.RedisHashCacheProperties;
import faang.school.urlshortenerservice.repository.postgre.PreparedUrlHashRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    @Qualifier("hashCacheRedisTemplate")
    private final RedisTemplate<String, String> hashCacheRedisTemplate;
    private final PreparedUrlHashRepository preparedUrlHashRepository;
    private final RedisHashCacheProperties properties;

    public Long addNewHashesToSet(Set<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return 0L;
        }

        Long addedCount = hashCacheRedisTemplate.opsForSet().add(properties.getKey(), hashes.toArray(new String[0]));
        log.info("HashCache: Added {} new hashes into Redis Set: {}.", addedCount, properties.getKey());

        return addedCount;
    }

    public String getAnyFirstHash() {
        String hash = hashCacheRedisTemplate.opsForSet().pop(properties.getKey());

        if (hash == null) {
            log.info("HashCache: Redis Set {} is empty.", properties.getKey());
            Set<String> newHash = preparedUrlHashRepository.findUntakenHashes(1);
            hash = newHash.stream()
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("There are no available hashes right now"));
            preparedUrlHashRepository.markHashesAsTaken(newHash);
        }

        return hash;
    }

    public long getCurrentSize() {
        Long size = hashCacheRedisTemplate.opsForSet().size(properties.getKey());
        return size != null ? size : 0;
    }

    public long getCapacity() {
        return properties.getCapacity();
    }
}