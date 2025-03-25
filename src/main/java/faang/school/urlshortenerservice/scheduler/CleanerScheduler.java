package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Value("${hash.cleanup.period}")
    private Period hashCleanupPeriod;

    @Scheduled(cron = "${scheduler.cleanup-cron}")
    public void deleteOldUrl() {
        LocalDateTime fromDate = LocalDateTime.now().minus(hashCleanupPeriod);
        urlService.deleteOldUrl(fromDate);
    }
}
