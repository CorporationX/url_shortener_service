package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduled {
    private final UrlService urlService;

    @Scheduled(cron = "${scheduler.cron}")
    public void cleaner() {
        urlService.deleteOldUrls();
    }
}
