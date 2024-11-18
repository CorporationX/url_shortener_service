package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private static UrlService urlService;

    @Scheduled(cron = "${hash.cron:0 0 6 * * *}")
    public void cleanUrl() {
        urlService.cleanUrl();
    }
}
