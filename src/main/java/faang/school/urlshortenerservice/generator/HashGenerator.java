package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.executor.ExecutorServiceConfig;
import faang.school.urlshortenerservice.config.propertis.hash.ThreadProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.CustomHashRepositoryImpl;
import faang.school.urlshortenerservice.repository.HashRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepositoryImpl hashRepositoryImpl;
    private final ThreadProperties threadProperties;
    private final Base62Encoder base62Encoder;
    private final CustomHashRepositoryImpl customHashRepository;
    private final ExecutorServiceConfig executor;

    @Value("${hash.generator.max-batch:50000}")
    private int maxBatch;
    @Value("${hash.generator.range-d-b:10000}")
    private long range;

    public CompletableFuture<Void> generateHash(long amount) {
        log.info("Starting generateHash with amount: {}", amount);
        return CompletableFuture
                .supplyAsync(() -> {
                    return hashRepositoryImpl.getUniqueNumbers(amount + range);
                }, executor.executor())
                .thenApply(base62Encoder::encode)
                .thenCompose(this::saveHashesInBatches);
    }

    public CompletableFuture<List<String>> getHashes(long amount) {
        log.info("Starting getHashes with amount: {}", amount);
        return CompletableFuture.supplyAsync(() -> {
                    return hashRepositoryImpl.findAndDelete(amount);
                }, executor.executor())
                .thenCompose(hashes -> {
                    long missing = amount - hashes.size();
                    log.debug("Hashes fetched: {}, missing: {}", hashes.size(), missing);
                    if (missing > 0) {
                        return generateHash(missing)
                                .thenCompose(v -> {
                                    log.debug("Fetching additional hashes from database...");
                                    hashes.addAll(hashRepositoryImpl.findAndDelete(missing));
                                    return CompletableFuture.completedFuture(hashes);
                                });
                    } else {
                        return CompletableFuture.completedFuture(hashes);
                    }
                })
                .thenApply(hashes -> {
                    log.debug("Converting hashes to strings...");
                    return hashes.stream().map(Hash::getHash).toList();
                });
    }

    private CompletableFuture<Void> saveHashesInBatches(List<Hash> hashes) {
        log.info("Saving {} hashes in batches...", hashes.size());
        BlockingDeque<Hash> batches = new LinkedBlockingDeque<>(hashes);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        int range = correctedBatch(hashes);

        while (!batches.isEmpty()) {
            List<Hash> batch = new ArrayList<>(range);
            int transferred = batches.drainTo(batch, range);
            log.debug("Transferred {} hashes to batch.", transferred);

            if (!batch.isEmpty()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    log.trace("Saving batch of {} hashes...", batch.size());
                    customHashRepository.saveAllBatched(batch);
                }, executor.executor()));
            }
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private int correctedBatch(List<Hash> hashes) {
        if (hashes.size() > maxBatch) {
            return hashes.size() / (threadProperties.getCorePoolSize() - 1);
        }
        return hashes.size();
    }
}
