package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlShortenerService urlShortenerService;

    @Scheduled(cron = "${hash-generation.scheduled_cleanup_rate}")
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastFor = "PT1M", lockAtMostFor = "PT5M")
    public void clean() {
        urlShortenerService.deleteCreatedAYearAgo();
    }
}
