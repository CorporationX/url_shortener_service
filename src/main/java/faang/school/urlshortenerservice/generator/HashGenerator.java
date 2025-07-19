package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    private final AtomicBoolean isHashDBGenerating = new AtomicBoolean(false);

    @Value("${hash.range:1000}")
    private int range;
    @Value("${hash.batch-size:100}")
    private int batchSize;

    @Transactional
    @Scheduled(cron = "${hash.schedule.cron:0 0/5 * * * ?}")
    public void generateHashes() {
        List<Long> nextRange = hashRepository.getUniqueNumbers(range);
        List<String> hashes = encoder.encode(nextRange);
        hashRepository.save(hashes);
    }

    @Transactional
    @Async("hashGenExecutor")
    public CompletableFuture<Void> generateHashesAsync() {
        generateHashes();
        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    @Async("hashGenExecutor")
    public CompletableFuture<List<Hash>> getHashBatch() {
        List<Hash> hashes = hashRepository.getHashBatch(batchSize);

        if (hashes.size() < batchSize && isHashDBGenerating.compareAndSet(false, true)) {
            generateHashesAsync().whenComplete((v, ex) -> {
                if (ex != null) {
                    log.error("Error during async hash generation", ex);
                }
                isHashDBGenerating.set(false);
            });
        }

        return CompletableFuture.completedFuture(hashes);
    }

    public List<Hash> getStartHashes() {
        generateHashes();
        return hashRepository.getHashBatch(batchSize);
    }
}
