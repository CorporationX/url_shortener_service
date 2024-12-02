package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    @Value("${scheduler.cleaning.url.expiration-interval}")
    private int expirationInterval;

    private final UrlService urlService;

    @Scheduled(cron = "${scheduler.cleaning.url.cron}")
    public void removingExpiredUrlsAndSavingHashes() {
        log.info("Started job removingExpiredUrlsAndSavingHashes in " + CleanerScheduler.class);
        urlService.jobForCleanerScheduler(expirationInterval);
    }

}
