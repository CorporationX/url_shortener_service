package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final ExecutorService hashGenerateExecutorService;
    @Value("${hash-generator.generate-count:1000}")
    private int generateCounts;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:1000}")
    private int batchSize;

    @Transactional
    public List<String> generateBatch() {
        log.info("Generating hashes");
        int quotientCount = generateCounts / batchSize;
        int remainderCount = generateCounts % batchSize;

        List<CompletableFuture<List<String>>> futures = IntStream.range(0, quotientCount)
                .boxed()
                .map(i ->
                        CompletableFuture.supplyAsync(() -> generateHashes(batchSize), hashGenerateExecutorService)
                ).collect(Collectors.toList());

        if (remainderCount > 0) {
            futures.add(CompletableFuture.supplyAsync(() -> generateHashes(remainderCount), hashGenerateExecutorService));
        }

        return CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(result -> (List<String>) result)
                .join();
    }

    private List<String> generateHashes(int count) {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(count);
        List<String> hashStrings = base62Encoder.encode(uniqueNumbers);
        List<Hash> hashes = hashStrings.stream()
                .map(Hash::new)
                .toList();
        List<Hash> savedHashes = hashRepository.saveAll(hashes);

        log.info("Saved hashes count: {}", savedHashes.size());

        return savedHashes.stream()
                .map(Hash::getHash)
                .toList();
    }
}
