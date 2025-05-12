package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${cleaner.cron}")
    private String cron;

    @Scheduled(cron = "${cleaner.cron}")
    @Transactional
    public void cleanExpiredUrls() {
        log.info("Starting cleanup job...");

        Instant cutoff = Instant.now().minus(Duration.ofDays(365));

        List<String> freedHashes = urlRepository.deleteOldUrlsBefore(Timestamp.from(cutoff));
        log.info("Deleted {} expired URLs", freedHashes.size());

        if (!freedHashes.isEmpty()) {
            hashRepository.saveHashes(freedHashes);
            log.info("Returned {} hashes back to pool", freedHashes.size());
        }
    }
}
