package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.redis.hash_cache.RedisHashCacheProperties;
import faang.school.urlshortenerservice.exceptions.NoAvailableHashesFound;
import faang.school.urlshortenerservice.repository.postgre.PreparedUrlHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {

    private final RedisTemplate<String, String> hashCacheRedisTemplate;
    private final PreparedUrlHashRepository preparedUrlHashRepository;
    private final RedisHashCacheProperties properties;

    public String get() {
        String hash = hashCacheRedisTemplate.opsForSet().pop(properties.getKey());

        if (hash == null) {
            log.info("Redis Set {} is empty.", properties.getKey());
            Set<String> newHash = preparedUrlHashRepository.findFreeHashes(1);
            hash = newHash.stream()
                    .findFirst()
                    .orElseThrow(() -> new NoAvailableHashesFound("There are no available hashes right now"));
            preparedUrlHashRepository.markHashesAsTaken(newHash);
        }

        return hash;
    }

    public void put(Set<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return;
        }

        Long addedCount = hashCacheRedisTemplate.opsForSet().add(properties.getKey(), hashes.toArray(new String[0]));
        log.info("Added {} new hashes into Redis Set: {}.", addedCount, properties.getKey());
    }

    public long getCurrentSize() {
        Long size = hashCacheRedisTemplate.opsForSet().size(properties.getKey());
        return size != null ? size : 0;
    }

    public boolean isNotEnoughHashes() {
        return getCurrentSize() < properties.getCapacity() / 10;
    }
}