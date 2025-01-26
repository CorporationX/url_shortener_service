package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${schedule.url-cleaner-scheduler.cron}")
    public void deleteOutdatedUrls() {
        log.info("Starting the daily deletion of outdated URLs");
        urlService.deleteOldRecordsAndSaveHashes();
    }

}
