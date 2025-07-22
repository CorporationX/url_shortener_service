package faang.school.urlshortenerservice.job;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanerHashJob {
    private final UrlService urlService;
    @Value("${app.job.hash.cleaner.min-seconds:31536000}")
    private long minSeconds;

    @Scheduled(cron = "${app.job.hash.cleaner.cron}")
    public void run() {
        LocalDateTime minDate = LocalDateTime.now().minusSeconds(minSeconds);
        urlService.removeExpiredAndSaveHashes(minDate);
    }
}
