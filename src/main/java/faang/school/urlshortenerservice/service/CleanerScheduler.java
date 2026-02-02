package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${url.cleanup.ttl}")
    private Duration ttl;

    @Scheduled(cron = "${url.cleanup.cron}")
    public void cleanExpiredUrls() {
        Instant threshold = Instant.now().minus(ttl);

        List<String> expiredHashes = urlRepository.deleteOutdatedAndReturnHashes(threshold);

        if (expiredHashes.isEmpty()) {
            return;
        }

        hashRepository.saveHashes(expiredHashes);
        log.info("cleaner removed {} expired urls", expiredHashes.size());
    }
}
