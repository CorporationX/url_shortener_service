package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.property.UrlShortenerProperties;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlShortenerProperties properties;
    private final UrlService urlService;
    private final HashService hashService;

    @Scheduled(cron = "${url-shortener-properties.hash-active-cron}")
    @Transactional
    public void cleanOldUrls() {
        List<Url> oldUrls = urlService.pollOldUrls(LocalDateTime.now().minus(properties.getHashedUrlLifetime()));
        hashService.saveHashBatch(oldUrls.stream()
                .map(url -> {
                    Hash hash = new Hash();
                    hash.setHash(url.getHash());
                    return hash;
                })
            .toList());
    }
}
