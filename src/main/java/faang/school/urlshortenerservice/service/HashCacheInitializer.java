package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

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
                hashGenerationService.generateHash(needed);
            }
            hashQueueManager.refillQueueFromData();
        } catch (Exception ex) {
            log.error("Initialization failed", ex);
            throw new RuntimeException("Cache initialization error", ex);
        }
    }
}
