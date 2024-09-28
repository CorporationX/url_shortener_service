package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.cache.batchSize}")
    private int batchSize;


    @Transactional
    public void generateHash() {
        var range = hashRepository.getNextRange(batchSize);
        var hashes = encoder.encode(range);

        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long hashSize) {
        generateHash();
        return hashRepository.findAndDelete(hashSize);
    }

    @Transactional
    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long hashSize) {
        return CompletableFuture.completedFuture(getHashes(hashSize));
    }
}
