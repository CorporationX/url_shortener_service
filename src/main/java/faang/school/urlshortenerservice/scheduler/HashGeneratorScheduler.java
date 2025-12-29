package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashCacheService;
import faang.school.urlshortenerservice.service.HashCacheServiceRedis;
import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorScheduler {

    private final HashGenerator hashGenerator;
    private final HashCacheService hashCacheService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeOnStartup() {
        log.info("Application ready - initializing hash pool");
        try {
            for (int i = 0; i < 3; i++) {
                hashGenerator.generateHashBatch();
            }
            log.info("Initial hash pool generation completed");
        } catch (Exception e) {
            log.error("Failed to initialize hash pool on startup", e);
        }
    }

    @Scheduled(cron = "${hash.generator.cron:0 * * * * ?}")
    @SchedulerLock(
            name = "generateHashBatch",
            lockAtMostFor = "5m",
            lockAtLeastFor = "30s"
    )
    public void generateHashBatch() {
        log.info("Scheduled job started (with lock): generating hash batch");
        try {
            hashGenerator.generateHashBatch();

            if (hashCacheService instanceof HashCacheServiceRedis) {
                ((HashCacheServiceRedis) hashCacheService).fillRedisPool();
            }
            
            log.info("Scheduled hash generation job completed successfully");
        } catch (Exception e) {
            log.error("Scheduled hash generation job failed with error", e);
        }
    }
}

