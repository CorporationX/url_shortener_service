package faang.school.urlshortenerservice.scheduler;


import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@RequiredArgsConstructor
@Component
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${scheduler.cleaner}")
    @Async("executor")
    public void scheduledClean(){
        log.info("Scheduled url cleaner started");
        urlService.cleanAsync();
        log.info("Scheduled url cleaner finished");

    }
}