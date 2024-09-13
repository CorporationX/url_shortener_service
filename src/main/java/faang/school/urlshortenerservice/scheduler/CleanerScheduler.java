package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;
    @Value("${generator.scheduled.removedPeriod}")
    private String removedPeriod;

    @Scheduled(cron = "${generator.delete.url.scheduled.cron}")
    public void deleteOldURL() {
        urlService.deleteOldURL(removedPeriod);
    }
}