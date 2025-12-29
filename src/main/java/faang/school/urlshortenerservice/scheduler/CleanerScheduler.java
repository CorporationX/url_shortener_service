package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlCleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlCleanerService urlCleanerService;

    @Scheduled(cron = "${cleaner.cron}")
    @SchedulerLock(
            name = "cleanOldUrls",
            lockAtMostFor = "1h",  
            lockAtLeastFor = "5m"  
    )
    public void cleanOldUrls() {
        log.info("Scheduled job started (with lock): cleaning old URLs");
        try {
            urlCleanerService.cleanOldUrls();
            log.info("Scheduled job completed successfully");
        } catch (Exception e) {
            log.error("Scheduled job failed with error", e);
        }
    }
}