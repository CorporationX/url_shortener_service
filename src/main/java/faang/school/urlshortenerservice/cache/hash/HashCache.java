package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.generator.hash.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashGenerator hashGenerator;
    private final ExecutorService generateBatchPool;
    private CompletableFuture<Void> loadingTask = CompletableFuture.completedFuture(null);

    @Value("${value.getBatchSize}")
    private int getBatchSize;
    @Value("${value.minValueRedis}")
    private double minValueRedis;
    @Value("${value.hashPattern}")
    private String hashPattern;


    private int twentyPercent;

    @PostConstruct
    private void init() {
        twentyPercent = (int) Math.floor((double) getBatchSize * minValueRedis);
        List<String> newHashes = hashGenerator.generateBatch();
        saveToCache(hashPattern, newHashes);
    }

    public void saveToCache(String pattern, List<String> values) {
        values.forEach(value -> {
            String key = pattern + value;
            redisTemplate.opsForValue().set(key, value);
        });
    }

    public void saveToCache(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public synchronized String getRandomHashFromCache() {
        Set<String> keys = redisTemplate.keys(hashPattern + "*");

        if (keys == null || keys.isEmpty()) {
            return null;
        }

        int randomIndex = new Random().nextInt(keys.size());
        String randomKey = keys.toArray(new String[0])[randomIndex];
        String value = (String) redisTemplate.opsForValue().get(randomKey);
        redisTemplate.delete(randomKey);

        if (keys.size() <= twentyPercent && loadingTask.isDone()) {
            loadingTask = CompletableFuture.runAsync(() -> {
                try {
                    List<String> newHashes = hashGenerator.generateBatch();
                    saveToCache(hashPattern, newHashes);
                } catch (Exception e) {
                    log.error("Error generating batch and saving to cache", e);
                }
            }, generateBatchPool);
        }

        return value;
    }
}
