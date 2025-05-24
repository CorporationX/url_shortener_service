package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${urls-clean-job.entity-ttl-seconds:31536000}")
    @SuppressWarnings("unused")
    private int entityTtlInSeconds;

    @Scheduled(cron = "${urls-clean-job.timetable:0 0 0 * * ?}")
    public void restoreUnusedHashes() {
        var expiredAt = LocalDateTime.now().minusSeconds(entityTtlInSeconds);

        try {
            processUrlCleanupTransaction(expiredAt);
        } catch (Exception ex) {
            log.error("Failed to restore expired hashes: {}", ex.getMessage(), ex);
        }
    }

    @Transactional
    private void processUrlCleanupTransaction(LocalDateTime expiredAt) {
        var expiredHashes = urlRepository.deleteAndGetExpiredEntities(expiredAt)
                .stream()
                .map(url -> new Hash(url.getHash()))
                .toList();
        hashRepository.saveAll(expiredHashes);
    }
}
