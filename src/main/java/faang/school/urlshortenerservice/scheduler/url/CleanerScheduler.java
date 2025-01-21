package faang.school.urlshortenerservice.scheduler.url;

import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Transactional
    @Async("urlCleaningExecutor")
    @Scheduled(cron = "${url.cleaning.cron}")
    public void cleanExpiredUrls() {
        log.info("Cleaning expired URLs scheduled task started");
        urlService.cleanExpiredUrls();
        log.info("Cleaning expired URLs scheduled task finished");
    }
}
