package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCacheInitializer {
    private final HashRepository hashRepository;
    private final HashGenerationService hashGenerationService;
    private final HashQueueManager hashQueueManager;
    private final HashCacheProperties properties;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        try {
            int currentCount = hashRepository.getHashesCount();
            int targetCount = properties.getMaxDbMultiplier() * properties.getMaxSize();

            if (currentCount < targetCount) {
                int needed = targetCount - currentCount;
                int batches = (int) Math.ceil((double) needed / properties.getMaxGenerationBatch());

                log.info("Starting hash generation. Needed: {}, Batches: {}", needed, batches);
                generateHashesInBatches(batches);
            }

            fillQueueSingleAttempt();
        } catch (Exception ex) {
            log.error("Initialization failed", ex);
            throw new RuntimeException("Cache initialization error", ex);
        }
    }

    private void generateHashesInBatches(int batches) {
        for (int i = 0; i < batches; i++) {
            hashGenerationService.generateHash(0);
            log.info("Generated batch {}/{}", i + 1, batches);
        }
    }

    private void fillQueueSingleAttempt() {
        int target = properties.getMaxSize();
        hashQueueManager.refillFromDatabase();

        int actual = hashQueueManager.getCurrentHash();
        if (actual < target) {
            log.warn("Queue not fully filled: {}/{}", actual, target);
        } else {
            log.info("Queue filled successfully: {}", actual);
        }
    }
}
