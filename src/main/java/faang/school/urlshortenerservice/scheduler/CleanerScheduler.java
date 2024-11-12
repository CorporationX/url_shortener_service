package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.cleaner.CleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final CleanerService cleanerService;

    @Scheduled(cron = "${scheduler.cron}")
    public void clearExpiredUrls() {
        log.info("clearExpiredUrls() - start");
        cleanerService.clearExpiredUrls();
        log.info("clearExpiredUrls() - finish");
    }
}
