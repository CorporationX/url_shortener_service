package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;
    @Qualifier("hashExecutor")
    private final Executor hashExecutor;

    @Value("${hash.generator.batch-size}")
    private  int maxSize;

    @Transactional
    public List<String> generateNewHashes() {
        log.info("Generating a batch of {} hashes", maxSize);
        List<Long> ids = hashRepository.getUniqueNumbers(maxSize);
        List<String> hashes = base62Encoder.encode(ids);
        hashRepository.saveAll(
                hashes.stream()
                        .map(Hash::new)
                        .toList()
        );
        log.info("Saved {} new hashes to DB.", hashes.size());
        return hashes;
    }

    @Transactional
    public List<String> getHashes(int batchSize) {
        List<String> result = hashRepository.getHashBatch(batchSize);
        if (result.size() < batchSize) {
            result.addAll(hashRepository.getHashBatch(batchSize - result.size()));
        }
        return result;
    }

    @Async("hashExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int maxSize) {
        return CompletableFuture.supplyAsync(() -> getHashes(maxSize), hashExecutor);
    }
}
