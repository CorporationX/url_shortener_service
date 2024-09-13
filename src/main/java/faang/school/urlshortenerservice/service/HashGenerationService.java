package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.BaseEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HashGenerationService {
    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Value("${hash.batch-size}")
    private int batchSize;

    @Async("hashGenerationTaskExecutor")
    public void generateBatch() {
        generateNewHashes(batchSize);
    }
    @Async("hashGenerationTaskExecutor")
    public void generateNewHashes(long amount) {
        List<Long> uniqSequence = hashRepository.getUniqueNumbers(amount);
        List<Hash> generatedHashes = uniqSequence.stream()
                .map(baseEncoder::encode)
                .map(Hash::new).toList();
        hashRepository.saveBatchHashes(generatedHashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        long count = hashRepository.countHashes();
        if (count <= amount) {
            generateNewHashes(amount - count + 1);
        }
        return hashRepository.getHashBatch(amount).stream().map(Hash::getHash).toList();
    }

    @Transactional
    @Async("hashCacheTaskExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}
