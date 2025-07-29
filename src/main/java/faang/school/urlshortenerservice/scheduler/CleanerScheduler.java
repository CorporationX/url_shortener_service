package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${cleaner.cron.cleanTime:0 * * * * ?}")
    @SchedulerLock(name = "CleanerScheduler_cleanOldUrls",
            lockAtLeastFor = "PT2M", lockAtMostFor = "PT10M")
    public void cleanOldUrls() {
        log.info("Starting cleaning of obsolete URLs...");
        urlService.deleteOldUrls();
    }
}
