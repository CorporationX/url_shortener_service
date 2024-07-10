package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {

    @Value("${services.hash.batch}")
    private int batchSize;
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Override
    @Transactional
    @Async("generateHashesExecutor")
    public CompletableFuture<Void> generateHashes() {
        List<Hash> hashes = encoder.encodeSymbolsToHash(hashRepository.findUniqueNumbers(batchSize)).stream()
                .map(Hash::new)
                .toList();
        System.out.println("Hashes generated: " + hashes);
        hashRepository.saveAll(hashes);

        log.info("Generated new hashes batch");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Transactional
    public List<String> getHashes(int amount) throws RuntimeException {
        try {
            return hashRepository.getHashBatch(amount);
        } catch (Exception e) {
            log.error("Error getting hashes", e);
            throw new RuntimeException("Error getting hashes", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Async("generateBatchExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int amount) throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getHashes(amount);
            } catch (Exception e) {
                log.error("Error getting hashes asynchronously", e);
                throw new RuntimeException("Error getting hashes asynchronously", e);
            }
        });
    }
}