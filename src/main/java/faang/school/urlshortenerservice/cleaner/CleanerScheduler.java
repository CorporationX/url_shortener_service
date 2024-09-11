package faang.school.urlshortenerservice.cleaner;

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

    @Scheduled(cron = "${spring.cleaner.cron}")
    public void cleanUrl() {
        log.info("Urls cleaning");
        urlService.deleteOlderUrls();
        log.info("Urls cleaned");
    }
}
