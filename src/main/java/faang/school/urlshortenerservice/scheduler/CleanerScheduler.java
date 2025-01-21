package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;

    @Scheduled(cron = "${scheduler.cron.expression}")
    public void cleanupOldUrl() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        urlService.cleanupOldUrl(oneYearAgo);
    }
}
