package faang.school.urlshortenerservice.component;

import com.google.common.collect.Lists;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueNumberSeqRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final UniqueNumberSeqRepository sequenceRepository;
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final HashCreator hashCreator;

    @Value("${hash-generator-settings.count-returning-unique-numbers}")
    private int count;

    @Value("${hash-generator-settings.batch-size-processing-hashes}")
    private int batchSize;

    @Transactional
    public void generateBatch() {
        log.info("Starting {} hashes generation", count);
        List<Long> numbers = sequenceRepository.getUniqueNumbers(count);
        List<String> uniqueStrings = encoder.encode(numbers);

        List<CompletableFuture<List<Hash>>> futures = getCompletableFuturesHash(uniqueStrings);

        List<Hash> allHashes = futures.stream()
                .flatMap(future -> future.join().stream())
                .toList();
        hashRepository.saveAll(allHashes);
        log.info("Finished {} hashes generation", count);
    }

    private List<CompletableFuture<List<Hash>>> getCompletableFuturesHash(List<String> uniqueStrings) {
        List<CompletableFuture<List<Hash>>> futures = new ArrayList<>();
        List<List<String>> batches = Lists.partition(uniqueStrings, batchSize);

        batches.forEach(batch -> {
            CompletableFuture<List<Hash>> future = hashCreator.createHashes(batch)
                    .exceptionally(ex -> {
                        log.error("Error creating hashes for batch: {}", batch, ex);
                        return List.of();
                    });

            futures.add(future);
        });
        return futures;
    }
}
