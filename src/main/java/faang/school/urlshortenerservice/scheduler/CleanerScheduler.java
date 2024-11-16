package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlShortenerService urlShortenerService;

    @Scheduled(cron = "${schedule.url_cleaner_interval}")
    public void cleanOldUrls() {
        urlShortenerService.cleanOldUrls();
    }
}
