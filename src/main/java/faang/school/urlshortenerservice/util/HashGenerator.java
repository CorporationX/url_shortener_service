package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.properties.short_url.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashProperties hashProperties;

    public List<String> getHashes(long count) {
        List<String> hashes = hashRepository.getHashBatch(count);
        if (hashes.size() < count) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(count - hashes.size()));
        }
        return hashes;
    }

    @Async("urlHashTaskExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long count) {
        return CompletableFuture.completedFuture(getHashes(count));
    }

    @Transactional
    public void generateBatch() {
        long hashBatchSize = hashProperties.getDbCreateBatchSize();
        log.info("Generating new {} hashes for urls...", hashBatchSize);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashBatchSize);
        List<String> urlHashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(urlHashes);
        log.info("Finished generating new {} hashes for urls!", hashBatchSize);
    }
}
