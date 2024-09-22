package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.manage.UrlSchedulerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlSchedulerManager urlSchedulerManager;
    @Value("${menageUrlShortener.month-after-which-url-is-deleted}")
    private long monthAfterWhichUrlDeleted;

    @Scheduled(cron = "${menageUrlShortener.delete-cron}", zone = "${menageUrlShortener.time-zone}")
    public void clearExpiredHash() {
        LocalDateTime dateExpired = LocalDateTime.now().minusMonths(monthAfterWhichUrlDeleted);
        urlSchedulerManager.removingExpiredHashes(dateExpired);
    }
}
