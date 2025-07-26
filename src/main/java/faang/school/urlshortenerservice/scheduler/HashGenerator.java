package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.redis.hash_cache.RedisHashCacheProperties;
import faang.school.urlshortenerservice.entity.PreparedUrlHash;
import faang.school.urlshortenerservice.repository.postgre.PreparedUrlHashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {

    private final RedisHashCacheProperties properties;
    private final Base62Encoder base62Encoder;
    private final HashCache hashCache;
    private final PreparedUrlHashRepository preparedUrlHashRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Transactional
    public Long generateMoreHashes(long startIndex, long amount) {
        log.info("{}: Generating hashes", Thread.currentThread().getName());
        Set<String> untakenHashes = preparedUrlHashRepository.findUntakenHashes((int) amount);

        long hashesRemainder = amount - untakenHashes.size();
        int amountOfFullBatches = (int) (hashesRemainder / properties.getBatchSize());
        int remainder = (int) (hashesRemainder % properties.getBatchSize());
        if (remainder > 0) amountOfFullBatches++;
        List<CompletableFuture<Set<String>>> futures = new ArrayList<>();
        long currentStartIndex = startIndex;
        if (hashesRemainder > 0) {
            for (int i = 0; i < amountOfFullBatches; i++) {
                long toIndex;
                if (remainder > 0 && i == amountOfFullBatches - 1) {
                    toIndex = currentStartIndex + remainder;
                } else {
                    toIndex = currentStartIndex + properties.getBatchSize();
                }
                long finalCurrentStartIndex = currentStartIndex;
                futures.add(CompletableFuture.supplyAsync(() -> generateHashesAsync(finalCurrentStartIndex, toIndex), taskExecutor));
                currentStartIndex = toIndex;
            }
        }

        Set<String> newHashesBatch = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet())
                ).join();

        preparedUrlHashRepository.markHashesAsTaken(untakenHashes);
        addNewHashesIntoDatabase(newHashesBatch);

        newHashesBatch.addAll(untakenHashes);
        Long hashesAddedToCache = hashCache.addNewHashesToSet(newHashesBatch);

        if (hashesAddedToCache != null) {
            log.info("HashGenerator: Added {} hashes into cache, out of {} prepared hashes",
                    hashesAddedToCache, newHashesBatch.size());
        } else {
            log.info("No hashes were added to the cache");
            // Do a rollback?
        }

        return currentStartIndex;
    }

    private Set<String> generateHashesAsync(long fromIndex, long toIndex) {
        log.info("{}: Generating hashes from {} to {}", Thread.currentThread().getName(), fromIndex, toIndex);

        return LongStream.range(fromIndex, toIndex)
                .mapToObj(i -> base62Encoder.encode(i, properties.getHashLength())
                ).collect(Collectors.toSet());
    }

    private void addNewHashesIntoDatabase(Set<String> batchOfHashes) {
        Set<PreparedUrlHash> preparedUrlHashes = batchOfHashes.stream()
                .map(hash -> PreparedUrlHash.builder()
                        .hash(hash)
                        .taken(true)
                        .build())
                .collect(Collectors.toSet());

        try {
            preparedUrlHashRepository.saveAll(preparedUrlHashes);
            log.info("HashGenerator: Initial hash generation complete. Total available hashes: {}",
                    preparedUrlHashes.size());
        } catch (Exception e) {
            log.info("HashGenerator: Error saving hashes to database: ", e);
        }
    }
}