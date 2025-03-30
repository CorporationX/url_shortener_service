package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Value("${hash.max-limit}")
    private int hashLimit;

    @Transactional
    public CompletableFuture<Void> generateBatches() {
        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashLimit);
            String[] hashes = encoder.generateHashes(uniqueNumbers);
            hashRepository.saveHashes(hashes);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
