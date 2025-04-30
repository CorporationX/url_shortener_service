package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashScheduler {
    private final HashService hashService;

    @Value("${hash.generator.batch-size}")
    private int batchSize;

    @Value("${hash.generator.max-size}")
    private int maxSize;

    @Value("${hash.scheduler.min-available-percentage}")
    private int minPercentage;

    @Async("taskExecutor")
    @Scheduled(cron = "${hash.scheduler.generate-cron}")
    public void generateHashes() {
        long availableHashPercentage = hashService.getHashCount() / maxSize * 100;
        if (availableHashPercentage < minPercentage) {
            hashService.generateHashes(batchSize);
        }
    }

    @Async("taskExecutor")
    @Scheduled(cron = "${hash.scheduler.generate-max-cron}")
    public void generateMaxHashes() {
        int missingHashes = (int) (maxSize - hashService.getHashCount());
        if (missingHashes > 0) {
            hashService.generateHashes(missingHashes);
        }
    }
}
