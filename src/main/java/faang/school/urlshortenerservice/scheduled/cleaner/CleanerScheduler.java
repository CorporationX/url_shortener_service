package faang.school.urlshortenerservice.scheduled.cleaner;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${scheduled.cron_cleaning}")
    public void cleaner() {
        urlService.cleaner();
    }

}
