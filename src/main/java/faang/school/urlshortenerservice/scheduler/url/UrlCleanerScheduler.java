package faang.school.urlshortenerservice.scheduler.url;

import faang.school.urlshortenerservice.service.url.UrlCleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UrlCleanerScheduler {
    private final UrlCleanerService urlCleanerService;

    @Scheduled(cron = "${app.scheduler.url_cleaner.cron}")
    public void execute() {
        urlCleanerService.removeExpiredUrls();
    }
}
