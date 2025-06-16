package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.urlcleanup.UrlCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlCleanupService urlCleanupService;

    @Scheduled(cron = "${url.cleanup.cron:0 0 0 * * *}")
    public void scheduledUrlCleanup() {
        urlCleanupService.cleanExpiredUrls();
    }
}
