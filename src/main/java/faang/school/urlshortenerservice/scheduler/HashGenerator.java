package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.redis.hash_cache.RedisHashCacheProperties;
import faang.school.urlshortenerservice.entity.PreparedUrlHash;
import faang.school.urlshortenerservice.repository.postgre.PreparedUrlHashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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

    @Transactional
    public long generateMoreHashes(long startIndex, long amount) {
        Set<String> untakenHashes = preparedUrlHashRepository.findUntakenHashes((int) amount);

        long hashesRemainder = amount - untakenHashes.size();
        int amountOfFullBatches = (int) (hashesRemainder / properties.getBatchSize());
        int remainder = (int) (hashesRemainder % properties.getBatchSize());
        if (remainder > 0) amountOfFullBatches++;
        CompletableFuture<Set<String>>[] futures = new CompletableFuture[amountOfFullBatches];

        long currentStartIndex = startIndex;
        if (hashesRemainder > 0) {
            for (int i = 0; i < amountOfFullBatches; i++) {
                long toIndex = currentStartIndex + properties.getBatchSize();
                if (remainder > 0 && i == amountOfFullBatches - 1) {
                    toIndex = currentStartIndex + remainder;
                }
                log.info("i: {} | hashesRemainder:{} | amountOfFullBatches: {} | remainder: {} | currentStartIndex: {} | toIndex: {}",
                        i, hashesRemainder, amountOfFullBatches, remainder, currentStartIndex, toIndex);
                futures[i] = generateHashesAsync(currentStartIndex, toIndex);
                currentStartIndex = toIndex;
            }
        }

        Set<String> newHashesBatch = CompletableFuture.allOf(futures)
                .thenApply(v -> Arrays.stream(futures)
                        .map(CompletableFuture::join)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet()))
                .join();

        addNewHashesIntoDatabase(newHashesBatch);
        preparedUrlHashRepository.markHashesAsTaken(untakenHashes);

        newHashesBatch.addAll(untakenHashes);
        Long hashesAddedToCache = hashCache.addNewHashesToSet(newHashesBatch);

        if (hashesAddedToCache != null) {
            log.info("HashGenerator: Added {} hashes into cache, out of {} prepared hashes",
                    hashesAddedToCache, newHashesBatch.size());
        } else {
            log.info("No hashes were added to the cache");
        }

        return currentStartIndex;
    }

    @Async("taskExecutor")
    private CompletableFuture<Set<String>> generateHashesAsync(long fromIndex, long toIndex) {
        log.info("{}: Generating hashes from {} to {}", Thread.currentThread().getName(), fromIndex, toIndex);

        Set<String> generatedHashes = LongStream.range(fromIndex, toIndex)
                .mapToObj(i -> base62Encoder.encode(i, properties.getHashLength())
                ).collect(Collectors.toSet());

        return CompletableFuture.completedFuture(generatedHashes);
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