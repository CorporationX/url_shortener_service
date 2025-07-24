package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.redis.hash_cache.RedisHashCacheProperties;
import faang.school.urlshortenerservice.repository.postgre.PreparedUrlHashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGeneratorScheduler {

    private final RedisHashCacheProperties properties;
    private final HashCache hashCache;
    private final PreparedUrlHashRepository preparedUrlHashRepository;
    private final HashGenerator hashGenerator;

    private long indexOfPreparedHashes;

    @PostConstruct
    protected void init() {
        indexOfPreparedHashes = preparedUrlHashRepository.count();
        generateBatch();
    }

    @Scheduled(cron = "${cron.hash_generator.every_hour}")
    protected void generateScheduledBatch() {
        generateBatch();
    }

    public void generateBatch() {
        long currentCacheSize = hashCache.getCurrentSize();
        if (currentCacheSize < properties.getCapacity()) {
            log.info("CurrentCacheSize: {} | CACHE_CAPACITY: {}", currentCacheSize, properties.getCapacity());
            indexOfPreparedHashes = hashGenerator.generateMoreHashes(indexOfPreparedHashes,
                    properties.getCapacity() - currentCacheSize);
        } else {
            log.debug("HashGenerator: Sufficient hashes available ({}). Skipping scheduled generation.", currentCacheSize);
        }
    }
}