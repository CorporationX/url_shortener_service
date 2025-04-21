package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.property.UrlShortenerProperties;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlShortenerProperties properties;
    private final UrlService urlService;

    @Scheduled(cron = "${url-shortener-properties.hash-active-cron}")
    public void cleanOldUrls() {
        urlService.pollOldUrls(LocalDateTime.now().minus(properties.getHashedUrlLifetime()));
    }
}
