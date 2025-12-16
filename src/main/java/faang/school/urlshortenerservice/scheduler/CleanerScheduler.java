package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlShortenerService urlShortenerService;

    @Scheduled(cron = "${url.delete.cron}", zone = "Asia/Almaty")
    public void scheduleOldUrlDelete() {
        log.info("Task started");
        urlShortenerService.deleteOneYearOldUrl();
        log.info("Task finished");
    }

}
