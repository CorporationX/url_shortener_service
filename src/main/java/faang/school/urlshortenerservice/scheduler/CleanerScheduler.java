package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${scheduler.clean.cron}")
    @Async("SchedulerThreadPool")
    public void clearFreeHash() {
        urlService.refreshFreeHash();
    }
}
