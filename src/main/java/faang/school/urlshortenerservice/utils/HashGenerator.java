package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.redis.hash_cache.RedisHashCacheProperties;
import faang.school.urlshortenerservice.entity.PreparedUrlHash;
import faang.school.urlshortenerservice.repository.postgre.PreparedUrlHashRepository;
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
    private final HashCache hashCache;
    private final PreparedUrlHashRepository preparedUrlHashRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Transactional
    public Long generateHashes(long startIndex) {
        if (hashCache.getCurrentSize() >= properties.getCapacity()) return null;

        long amount = properties.getCapacity() - hashCache.getCurrentSize();
        Set<String> untakenHashes = preparedUrlHashRepository.findFreeHashes((int) amount);

        long hashesRemainder = amount - untakenHashes.size();
        int amountOfFullBatches = (int) (hashesRemainder / properties.getBatchSize());
        int remainder = (int) (hashesRemainder % properties.getBatchSize());
        if (remainder > 0) amountOfFullBatches++;

        List<CompletableFuture<Set<String>>> futures = new ArrayList<>();
        long currentStartIndex = startIndex;
        if (hashesRemainder > 0) {
            for (int i = 0; i < amountOfFullBatches; i++) {
                long toIndex = remainder > 0 && i == amountOfFullBatches - 1 ?
                        currentStartIndex + remainder :
                        currentStartIndex + properties.getBatchSize();

                futures.add(CompletableFuture.supplyAsync(() -> generateHashesAsync(startIndex, toIndex), taskExecutor));
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

        hashCache.put(newHashesBatch);

        return currentStartIndex;
    }

    private Set<String> generateHashesAsync(long fromIndex, long toIndex) {
        log.info("Generating hashes from index: {} to index: {}", fromIndex, toIndex);

        return LongStream.range(fromIndex, toIndex)
                .mapToObj(i -> Base62Encoder.encode(i, properties.getHashLength()))
                .collect(Collectors.toSet());
    }

    private void addNewHashesIntoDatabase(Set<String> batchOfHashes) {
        Set<PreparedUrlHash> preparedUrlHashes = batchOfHashes.stream()
                .map(hash -> new PreparedUrlHash(hash, true))
                .collect(Collectors.toSet());

        preparedUrlHashRepository.saveAll(preparedUrlHashes);
    }
}