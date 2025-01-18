package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final UniqueIdRepository uniqueIdRepository;
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${app.hash.generator.batch-size:1000}")
    private int generationBatch;

    @Transactional
    @Scheduled(cron = "${app.hash.generator.cron:0 0 0 * * *}")
    public void generateBatch() {
        log.info("Generating batch of {} hashes", generationBatch);
        List<Long> seeds = uniqueIdRepository.getNextRange(generationBatch);
        List<String> hashes = seeds.stream()
                .map(encoder::encode)
                .toList();
        hashRepository.saveHashes(hashes);
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = hashRepository.getHashes(amount);
        if (hashes.size() < amount) {
            log.warn("All pregenerated hashes were used");
            generateBatch();
            hashes.addAll(hashRepository.getHashes(amount - hashes.size()));
        }

        return hashes;
    }

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int amount) {
        return CompletableFuture.supplyAsync(() -> getHashes(amount));
    }
}
