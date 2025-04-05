package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash-cache.max.range:10000}")
    private int maxRange;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public void generateHashes() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = base62Encoder.encode(range);

        hashRepository.saveAll(hashes);
    }

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public List<Hash> getHashes(int count) {
        if (count <= 0) {
            String message = "Некорректное значение количества хэшей = " + count;
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        List<Hash> hashes = hashRepository.findAndDelete(count);
        if (count > hashes.size()) {
            generateHashes();
            hashes.addAll(hashRepository.findAndDelete(count - hashes.size()));
        }
        return hashes;
    }

    @Async(value = "threadPool")
    @Transactional
    public CompletableFuture<List<Hash>> getHashesAsync(int count) {
        List<Hash> hashes = getHashes(count);
        return CompletableFuture.completedFuture(hashes);
    }
}
