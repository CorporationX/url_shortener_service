package faang.school.urlshortenerservice.generator.hash;

import faang.school.urlshortenerservice.config.properties.hash.HashProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.model.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashProperties hashProperties;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        try {
            String threadName = Thread.currentThread().getName();
            int batchSize = hashProperties.getBatch().getGet();
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            log.debug("Thread {} , retrieved {} unique numbers!", threadName, uniqueNumbers.size());

            List<List<Long>> partitions = ListUtils.partition(uniqueNumbers, hashProperties.getThreadPool()
                    .getInitialPoolSize());

            List<CompletableFuture<List<Hash>>> futures = partitions.stream()
                    .map(base62Encoder::encode)
                    .map(CompletableFuture::completedFuture)
                    .toList();

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.thenAccept(v -> {
                List<Hash> hashes = futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Collection::stream)
                        .toList();

                hashRepository.saveAllBatched(hashes);
                log.debug("Thread {} , successfully saved hashes in DB!", threadName);
            }).exceptionally(ex -> {
                log.error("Error while encoding hashes! :", ex);
                return null;
            });

        } catch (DataAccessException dae) {
            log.error("Error while generating batch! :", dae);
            throw new RuntimeException("Error! " + dae.getMessage() + " ", dae);
        }
    }
}
