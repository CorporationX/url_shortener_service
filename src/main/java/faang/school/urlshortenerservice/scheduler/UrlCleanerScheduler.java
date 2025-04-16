package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UrlCleanerScheduler {
    private final UrlService urlService;

    @Scheduled(cron = "${scheduler.url_cleaner.cron}")
    public void execute() {
        urlService.removeExpiredUrlsAndResaveHashes();
    }
}