package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${url.cleaner.scheduler.cron}")
    private void cleanExpiredUrls() {
        log.info("CleanerScheduler#cleanExpiredUrls started...");
        urlService.cleanExpiredUrls();
        log.info("CleanerScheduler#cleanExpiredUrls finished.");
    }
}
