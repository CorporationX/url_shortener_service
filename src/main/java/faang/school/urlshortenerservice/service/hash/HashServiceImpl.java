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

    @Value("${batch-size.hash}")
    private Long batchSize;
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Override
    @Transactional
    @Async("generateHashesBatchExecutor")
    public CompletableFuture<Void> generateHashesBatch() {
        List<Hash> hashes = encoder.encodeSymbolsToHash(hashRepository.findUniqueNumbers(batchSize)).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);

        log.info("Generated new hashes batch");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getHashes(Long amount) {
        List<String> hashes = hashRepository.getHashBatch(amount);
        while (hashes.size() < amount) {
            generateHashesBatch().join();
            hashes.addAll(hashRepository.getHashBatch((amount - hashes.size())));
        }
        return hashes;
    }

    @Override
    @Transactional(readOnly = true)
    @Async("generateHashesBatchExecutor")
    public CompletableFuture<List<String>> getHashesAsync(Long amount) {
        return CompletableFuture.supplyAsync(() -> getHashes(amount));
    }
}