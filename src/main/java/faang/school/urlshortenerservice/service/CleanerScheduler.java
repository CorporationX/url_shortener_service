package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.RecordCleanupException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlJpaRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.cron.cleaner}")
    public void deleteOldRecords() {
        try {
            List<Hash> expiredHashes = urlRepository.deleteExpired();
            hashRepository.saveHashes(expiredHashes);
            log.info("Expired hashes are reused: {}", expiredHashes);
        } catch (Exception e) {
            log.error("Error during old hashes cleanup: {}", e.getMessage());
            throw new RecordCleanupException("Failed to cleanup old hashes", e);
        }
    }
}
