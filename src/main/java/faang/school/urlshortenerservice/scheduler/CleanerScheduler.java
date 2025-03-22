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

    @Scheduled(cron = "${cron.url-cleaner}")
    public void cleanOldUrls() {
        log.info("Starting cleaning old URLs");
        urlService.cleaningExpiredUrls();
        log.info("Finished cleaning old URLs");
    }
}
