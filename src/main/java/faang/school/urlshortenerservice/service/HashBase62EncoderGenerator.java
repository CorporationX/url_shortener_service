package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashBase62EncoderGenerator implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash.batch.size}")
    private int batchSize;

    @Override
    @Async("hashGeneratorThreadPool")
    @Transactional
    public CompletableFuture<List<String>> generateBatch() {
        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> hashes = base62Encoder.encodeBatch(uniqueNumbers);
            List<String> savedHashes = hashRepository.saveAllBatch(hashes.toArray(new String[0]));
            log.info("Generated {} new hashes", savedHashes.size());
            return CompletableFuture.completedFuture(savedHashes);
        } catch (Exception e) {
            log.error("Hash generation failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}