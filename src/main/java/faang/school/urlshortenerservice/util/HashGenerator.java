package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashService hashService;
    private final BaseEncoder baseEncoder;
    private final ThreadPoolTaskExecutor shortenerTaskExecutor;

    @Value("${hash-properties.generate-batch}")
    private Long batchSize;

    @Value("${hash-properties.cache-threshold-rate}")
    private double lowThresholdPercent;

    public CompletableFuture<Void> asyncHashRepositoryRefill() {
        return CompletableFuture.runAsync(() -> {
            if (isRefillNeeded()) {
                List<Long> uniqueNumbers = hashService.getUniqueSeqNumbers(batchSize);
                hashService.saveHashes(baseEncoder.encodeList(uniqueNumbers));
                log.info("Created and saved {} hashes", uniqueNumbers.size());
            }
        }, shortenerTaskExecutor);
    }

    private boolean isRefillNeeded() {
        return hashService.getHashRepositorySize() < batchSize * lowThresholdPercent;
    }
}
