package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    @Value("${hash.cleaner.after-days}")
    private int afterDays;

    @Transactional
    @Scheduled(cron = "${hash.cleaner.cron}")
    void cleanUpExpiredUrls() {
        try {
            Instant cutoffTime = Instant.now().minus(Duration.ofDays(afterDays));
            log.info("Starting cleanup process. Cutoff time: {}", cutoffTime);
            List<Hash> hashes = urlRepository.deleteUrlBeforeCreatedAt(cutoffTime)
                    .stream()
                    .map(Hash::new)
                    .toList();
            log.info("Found {} expired hashes to save", hashes.size());
            hashRepository.saveAll(hashes);
            log.info("Cleanup process completed successfully");
        } catch (Exception e) {
            log.error("Error during cleanup process", e);
            throw new RuntimeException("Failed to clean up expired URLs", e);
        }
    }
}