package faang.school.urlshortenerservice.config.scheduled;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;

    @Scheduled(cron = "${url.url-deleted.scheduler.cron}")
    public void removingExpiredUrls() {
        urlService.findAndDeleteExpiredUrls();
    }
}
