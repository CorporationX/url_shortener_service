package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlCleanupService urlCleanupService;

    @Scheduled(cron = "${spring.scheduler.cleanup.cron}")
    public void cleanupExpiredUrls() {
        urlCleanupService.cleanupExpiredUrls();
    }
}
