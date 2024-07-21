package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Transactional
    @Scheduled(cron = "${services.url.cleaner.cron}")
    public void cleanOldUrls() {
        urlService.deleteOldUrls();
    }
}
