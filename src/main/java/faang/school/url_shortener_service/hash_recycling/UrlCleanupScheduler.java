package faang.school.url_shortener_service.hash_recycling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCleanupScheduler {

    private final ExpiredUrlCleanupService expiredUrlCleanupService;

    @Scheduled(cron = "${url.cleaner.cron}")
    public void executeUrlCleanup() {
        log.info("Scheduled URL cleanup triggered on thread: {}", Thread.currentThread().getName());
        expiredUrlCleanupService.removeOldUrls();
    }
}