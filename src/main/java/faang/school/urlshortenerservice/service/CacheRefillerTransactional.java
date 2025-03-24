package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashJdbcRepository;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import faang.school.urlshortenerservice.repository.RedisShortenerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheRefillerTransactional {
    private final FreeHashRepository freeHashRepository;
    private final RedisShortenerRepository redisShortenerRepository;
    private final FreeHashGenerator freeHashGenerator;
    private final FreeHashJdbcRepository freeHashJdbcRepository;

    @Value("${shortener.hash-pool.max-capacity}")
    private int maxCapacity;

    @Transactional
    public void refillRedisAtStart() {
        List<Long> range = freeHashRepository.generateBatch(maxCapacity);
        refillRedisFromGenerator(range);
    }

    public void refillRedisFromGenerator(List<Long> capacity) {
        if (!redisShortenerRepository.tryLockForGeneration()) {
            return;
        }

        try {
            List<FreeHash> generated = freeHashGenerator.generateHashes(capacity);
            pushHashesToRedis(generated);
            log.info("pushed {} free hashes from generator to Redis", generated.size());
        } finally {
            redisShortenerRepository.releaseLock();
        }
    }

    @Transactional
    public void refillRedisFromDb(long capacity) {
        if (!redisShortenerRepository.tryLockForGeneration()) {
            return;
        }
        try {
            List<FreeHash> freeHashesFromDb = freeHashRepository
                    .findAndLockFreeHashes((int) capacity);

            pushHashesToRedis(freeHashesFromDb);

            freeHashJdbcRepository.deleteByIds(
                    freeHashesFromDb.stream()
                            .map(FreeHash::getHash)
                            .toList());
            log.info("pushed {} free hashes from DB to Redis", freeHashesFromDb.size());
        } finally {
            redisShortenerRepository.releaseLock();
        }
    }

    private void pushHashesToRedis(List<FreeHash> hashes) {
        List<String> hashStrings = hashes.stream()
                .map(FreeHash::getHash)
                .toList();

        redisShortenerRepository.saveFreeHashesBatch(hashStrings);
    }
}
