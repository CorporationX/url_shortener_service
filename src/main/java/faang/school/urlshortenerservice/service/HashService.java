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
            // TODO: не будет рабоать с текущими локами
            generateHashBatchIfNeeded();
            hashes.addAll(getHashList(count - hashes.size()));
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

    // TODO: альтернативы
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

            List<Long> numbers = hashRepository.getNextSequenceValues(missingCount);

            List<Hash> hashes = numbers.stream()
                    .map(base62Encoder::encode)
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(hashes);
        } finally {
            hashRepository.unlock(lockId);
        }
    }
}
