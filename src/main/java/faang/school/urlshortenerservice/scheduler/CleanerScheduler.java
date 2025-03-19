package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.LockRepository;
import faang.school.urlshortenerservice.repository.UrlCleanupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlCleanupRepository urlCleanupRepository;
    private final LockRepository lockRepository;

    @Value("${spring.scheduler.cleanup.lock-name}")
    private String lockName;

    @Scheduled(cron = "${spring.scheduler.cleanup.cron}")
    @Transactional
    public void cleanupExpiredUrls() {
        if (lockRepository.tryAcquireLock(lockName)) {
            List<String> expiredHashes = urlCleanupRepository.cleanupExpiredUrls();
            log.info("Moved {} expired hashes to hash table", expiredHashes.size());
        }
    }
}
