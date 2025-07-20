package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;

    @Scheduled(cron = "${app.scheduler.clean-unused.cron}")
    @SchedulerLock(
            name = "${app.scheduler.clean-unused.lock-name}",
            lockAtLeastFor = "${app.scheduler.clean-unused.lock-at-least-for}",
            lockAtMostFor = "${app.scheduler.clean-unused.lock-at-most-for}"
    )
    public void cleanUnused() {
        log.debug("Starting cleaning unused hashes");
        urlService.removeUnusedUrls();
    }
}
