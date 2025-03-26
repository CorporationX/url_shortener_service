package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.util.Base62Encoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.LongStream;

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

    @Value("${app.hash_generator.queue_key:url:hashes}")
    private String hashQueueKey;

    private long counter;

    @PostConstruct
    public void init() {
        this.counter = initialCounter;
        log.info("Initialized hash counter with value: {}", counter);
    }

    public synchronized String acquire() {
        Long size = redisTemplate.opsForList().size(hashQueueKey);
        if (size == null || size < maxSize * threshold) {
            int toGenerate = Math.max(minReplenishSize, (int) (maxSize - (size != null ? size : 0)));
            generateHashes(toGenerate);
        }

        String hash = redisTemplate.opsForList().rightPop(hashQueueKey);
        if (hash == null) {
            log.error("Redis hash pool exhausted: unable to provide hash.");
            throw new RuntimeException("No available hashes. Try again later.");
        }

        log.debug("Acquired hash from Redis pool: {}", hash);
        return hash;
    }

    private void generateHashes(int count) {
        List<Long> numbers = LongStream.range(counter, counter + count).boxed().toList();
        counter += count;
        List<String> hashes = encoder.encode(numbers);
        redisTemplate.opsForList().leftPushAll(hashQueueKey, hashes);
        log.info("Generated and pushed {} hashes to Redis. Counter now at: {}", hashes.size(), counter);
    }

    public void returnHashes(List<String> hashes) {
        if (!hashes.isEmpty()) {
            redisTemplate.opsForList().leftPushAll(hashQueueKey, hashes);
            log.info("Returned {} hashes back to Redis pool", hashes.size());
        } else {
            log.warn("Attempted to return an empty hash list to Redis pool");
        }
    }
}