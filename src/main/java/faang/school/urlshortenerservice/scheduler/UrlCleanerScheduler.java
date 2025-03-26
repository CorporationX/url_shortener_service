package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlCleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UrlCleanerScheduler {

    private final UrlCleanerService urlCleanerService;

    @Scheduled(cron = "${app.scheduler.url_cleaner.cron}")
    public void execute() {
        urlCleanerService.removeExpiredUrlsAndResaveHashes();
    }
}