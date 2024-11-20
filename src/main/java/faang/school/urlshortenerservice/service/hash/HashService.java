package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashService {
    @Value("${hash.batch}")
    private int batchSize;
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Async("generateHashesExecutor")
    public CompletableFuture<Void> generateAndStoreHashBatch() {
        List<Hash> hashes = encoder.encodeSymbolsToHash(hashRepository.findUniqueNumbers(batchSize)).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    public List<String> retrieveHashBatch(int amount) throws RuntimeException {
        try {
            return hashRepository.getHashBatch(amount);
        } catch (Exception e) {
            throw new RuntimeException("Error getting hashes", e);
        }
    }

    @Transactional(readOnly = true)
    @Async("generateBatchExecutor")
    public CompletableFuture<List<String>> retrieveHashBatchAsync(int amount) throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return retrieveHashBatch(amount);
            } catch (Exception e) {
                throw new RuntimeException("Error getting hashes asynchronously", e);
            }
        });
    }
}