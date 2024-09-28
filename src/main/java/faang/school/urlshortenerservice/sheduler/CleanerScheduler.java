package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${scheduler.cron.cleaner}")
    @Async
    public void cleanOldUrls() {
        urlService.cleanOldUrls();
    }

}
