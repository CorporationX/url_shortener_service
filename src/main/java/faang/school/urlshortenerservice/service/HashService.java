package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${app.hash.memory-cache-size:2000}")
    private int tableSize;
    @Value("${app.hash.lock-id}")
    private int lockId;

    public List<String> getHashes(int count) {
        List<String> hashes = getHashList(count);

        if (hashes.size() < count) {
            List<String> newHashes = hashRepository.getNextSequenceValues(count - hashes.size())
                    .stream()
                    .map(base62Encoder::encode)
                    .toList();
            hashes.addAll(newHashes);
        }
        return hashes;
    }

    private List<String> getHashList(int count) {
        return hashRepository.findAndDeleteLimit(count)
                .stream()
                .map(Hash::getHash)
                .toList();
    }

    @Async("fillingMemoryCacheExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int count) {
        return CompletableFuture.completedFuture(getHashes(count));
    }

    @Transactional
    public void generateHashBatchIfNeeded() {
        boolean lockAcquired = hashRepository.tryLock(lockId);
        if (!lockAcquired) {
            return;
        }

        try {
            long currentCount = hashRepository.count();
            int missingCount = (int) (tableSize - currentCount);
            if (missingCount <= 0) {
                return;
            }

            List<Hash> hashes = hashRepository.getNextSequenceValues(missingCount)
                    .stream()
                    .map(base62Encoder::encode)
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(hashes);
        } finally {
            hashRepository.unlock(lockId);
        }
    }
}
