package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final HashService hashService;

    @Scheduled(cron = "${scheduler.cleaner}")
//    @Scheduled(fixedDelay = 10000)
    @Async("taskExecutor")
    public void scheduledClean() {
        log.info("Scheduled url cleaner started");
        hashService.cleanAsync();
        log.info("Scheduled url cleaner finished");

    }
}
