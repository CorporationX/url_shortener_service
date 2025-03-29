package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;

    @Value("${url.cleaner.retention-period:365D}")
    private Duration retentionPeriod;

    @Scheduled(cron = "${url.cleaner.cron}")
    public void deleteOldUrls() {
        LocalDateTime afterDeleteDate = LocalDateTime.now().minus(retentionPeriod);
        urlService.deleteOldUrls(afterDeleteDate);
    }
}
