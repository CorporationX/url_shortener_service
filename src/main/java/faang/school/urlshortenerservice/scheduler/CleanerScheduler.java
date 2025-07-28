package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;

    @Value("${scheduler.cleaner.time_before_clean_old_hashes}")
    private int timeBeforeCleanOldHashes;

    @Scheduled(cron = "${scheduler.cleaner.cron_scheduler_to_clean_old_hashes}")
    @SchedulerLock(
            name = "${scheduler.properties.lock_name}",
            lockAtLeastFor = "${scheduler.properties.lock_at_least_for}",
            lockAtMostFor = "${scheduler.properties.lock_at_most_for}"
    )
    public void clean() {
        log.debug("Scheduled clean for reuse old hashes was started");
        urlService.reuseOldUrls(timeBeforeCleanOldHashes);
        log.debug("Scheduled clean for reuse old hashes was finished");
    }
}