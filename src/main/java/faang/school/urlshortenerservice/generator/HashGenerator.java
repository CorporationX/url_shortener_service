package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

    @Transactional
    public List<String> generateNewHashes(int batchSize) {
        log.info("Generating a batch of {} hashes", batchSize);
        List<Long> ids = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(ids);
        hashRepository.saveAll(
                hashes.stream()
                        .map(Hash::new)
                        .toList()
        );
        log.info("Saved {} new hashes to DB.", hashes.size());
        return hashes;
    }

    @Async("hashExecutor")
    public CompletableFuture<List<String>> generateBatchAsync(int batchSize) {
        return CompletableFuture.supplyAsync(() -> generateNewHashes(batchSize), hashExecutor);
    }
}
