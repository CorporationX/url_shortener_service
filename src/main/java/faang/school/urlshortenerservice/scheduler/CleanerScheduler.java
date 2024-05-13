package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {


    private final UrlShortenerService urlShortenerService;

    //    @Scheduled(cron = "${scheduler.cleaner}")
    @Scheduled(fixedDelay = 10000)
    @Async("executor")
    public void scheduledClean() {
        log.info("Scheduled url cleaner started");
        urlShortenerService.cleanAsync();
        log.info("Scheduled url cleaner finished");

    }
}
