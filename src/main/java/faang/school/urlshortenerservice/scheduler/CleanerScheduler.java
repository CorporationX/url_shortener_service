package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${url-shortener.cleaning-cron}")
    public void cleanHashes() {
        urlService.cleanHashes();
    }
}
