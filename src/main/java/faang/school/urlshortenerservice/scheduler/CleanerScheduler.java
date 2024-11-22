package faang.school.urlshortenerservice.scheduler;


import faang.school.urlshortenerservice.service.cleanerService.CleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final CleanerService cleanerService;

    @Scheduled(cron = "${scheduler.cron}")
    @Async("urlThreadPool")
    public void clearExpiredUrls() {
        log.info("clearExpiredUrls() - start");
        cleanerService.clearExpiredUrls();
        log.debug("clearExpiredUrls() - finish");
    }
}
