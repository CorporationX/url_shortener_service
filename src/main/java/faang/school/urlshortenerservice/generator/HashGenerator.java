package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exeption.HashGenerationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.BatchProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final BatchProcessor<Long> hashBatchProcessor;

    @Value("${hash.range:1000000}")
    private int maxRange;


    @Transactional
    public void generateHashes() {
        try {
            List<Long> range = hashRepository.getNextRange(maxRange);
            hashBatchProcessor.processBatches(range, this::processBatch);
        } catch (Exception ex) {
            String errorMessage = "Error generate hashes: " + ex.getMessage();
            log.error(errorMessage);
            throw new HashGenerationException(errorMessage, ex);
        }
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHashes();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }


    private void processBatch(List<Long> batch) {
        List<Hash> hashes = batch.stream()
                .map(id -> new Hash(base62Encoder.encode(id)))
                .collect(Collectors.toList());
        hashRepository.saveAllBatch(hashes);
    }

}
