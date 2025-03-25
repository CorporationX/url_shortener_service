package faang.school.urlshortenerservice.job;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlShortenerProperties properties;
    private final UrlService urlService;
    private final HashService hashService;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(cron = "${url-shortener-service.clean-old-url-cron}")
    public void cleanOldUrls() {
        transactionTemplate.executeWithoutResult(status -> {
            log.info("Cleaning old urls");
            List<Url> oldUrls = urlService.getAndDeleteOldUrls(LocalDateTime.now().minus(properties.getUrlToCleanOlderThan()));
            log.info("Cleaned {} urls", oldUrls.size());
            hashService.saveHashesBatch(oldUrls.stream()
                    .map(url -> new Hash(url.getHash()))
                    .toList());
        });
    }
}
