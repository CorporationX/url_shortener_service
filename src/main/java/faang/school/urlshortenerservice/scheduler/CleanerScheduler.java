package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Async("threadPool")
    @Scheduled(cron = "${url-shortener.scheduler.delete-old-url}")
    public void deleteOldUrl() {
        urlService.deleteOldUrl();
    }
}
