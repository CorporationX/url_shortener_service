package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    @Value("${application.hashGenerator.batchSize:100}")
    private int batchSize;

    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> generateBatch() {
        log.info("Starting to generate batch of {} hashes", batchSize);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);

        List<Hash> hashes = encoder.encode(uniqueNumbers).stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);

        return CompletableFuture.completedFuture(hashes.stream().map(Hash::getHash).toList());
    }
}
