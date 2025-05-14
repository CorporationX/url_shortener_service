package faang.school.urlshortenerservice.service;

import com.google.common.collect.Lists;
import faang.school.urlshortenerservice.component.Base62Encoder;
import faang.school.urlshortenerservice.component.HashCreator;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.properties.HashGeneratorProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueNumberSeqRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGeneratorService {

    private final UniqueNumberSeqRepository sequenceRepository;
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final HashCreator hashCreator;
    private final HashCacheService hashCacheService;
    private final HashGeneratorProperties properties;

    private int count;

    @PostConstruct
    public void init() {
        count = properties.countReturningUniqueNumbers();
    }

    @Transactional
    public void generateHashes() {
        if (!hashRepository.existsHashesAtLeast(properties.availableHashesOnRepository())) {
            log.info("Starting {} hashes generation", count);
            List<Long> numbers = sequenceRepository.getUniqueNumbers(count);
            List<String> hashes = encoder.encode(numbers);
            processAllHashes(hashes);
            log.info("Finished {} hashes generation", count);
            hashCacheService.refillQueueSync();
        }
    }

    public void processAllHashes(List<String> hashes) {
        List<CompletableFuture<List<Hash>>> futures = processHashesOnBatches(hashes);

        List<Hash> allHashes = futures.stream()
                .flatMap(future -> future.join().stream())
                .toList();
        hashRepository.saveAll(allHashes);
    }

    private List<CompletableFuture<List<Hash>>> processHashesOnBatches(List<String> uniqueStrings) {
        List<CompletableFuture<List<Hash>>> futures = new ArrayList<>();
        List<List<String>> batches = Lists.partition(uniqueStrings, properties.batchSizeProcessingHashes());

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
