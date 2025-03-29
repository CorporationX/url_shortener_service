package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.util.Base62Encoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisHashPoolService {

    private final StringRedisTemplate redisTemplate;
    private final Base62Encoder encoder;

    @Value("${app.hash_generator.max_size}")
    private int maxSize;

    @Value("${app.hash_generator.replenish_threshold}")
    private double threshold;

    @Value("${app.hash_generator.initial_counter}")
    private long initialCounter;

    @Value("${app.hash_generator.min_replenish_size}")
    private int minReplenishSize;

    @Value("${app.hash_generator.queue_key}")
    private String hashSetKey;

    private AtomicLong counter;

    @PostConstruct
    public void init() {
        this.counter = new AtomicLong(initialCounter);
        log.info("Initialized hash counter at {}", counter.get());
    }

    public String acquire() {
        String hash = redisTemplate.opsForSet().pop(hashSetKey);
        if (hash != null) {
            log.debug("Acquired hash from Redis: {}", hash);
            return hash;
        }

        long value = counter.getAndIncrement();
        hash = encoder.encodeSingle(value);
        log.warn("Redis pool empty, fallback to local counter: {}", hash);
        return hash;
    }

    public void returnHashes(List<String> hashes) {
        if (!hashes.isEmpty()) {
            redisTemplate.opsForSet().add(hashSetKey, hashes.toArray(new String[0]));
            log.info("Returned {} hashes back to Redis pool", hashes.size());
        }
    }

    public void maybeReplenishPool() {
        Long size = redisTemplate.opsForSet().size(hashSetKey);
        if (size == null) size = 0L;
        if (size >= maxSize * threshold) return;

        int toGenerate = Math.max(minReplenishSize, (int) (maxSize - size));
        for (int i = 0; i < toGenerate; i++) {
            long value = counter.getAndIncrement();
            String hash = encoder.encodeSingle(value);
            redisTemplate.opsForSet().add(hashSetKey, hash);
        }

        log.info("Replenished Redis pool with {} hashes", toGenerate);
    }
}