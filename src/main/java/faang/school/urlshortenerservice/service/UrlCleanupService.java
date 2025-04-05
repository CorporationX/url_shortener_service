package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.LockRepository;
import faang.school.urlshortenerservice.repository.UrlCleanupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCleanupService {
    private final UrlCleanupRepository urlCleanupRepository;
    private final LockRepository lockRepository;

    @Value("${spring.scheduler.cleanup.lock-name}")
    private String lockName;

    @Transactional
    public void cleanupExpiredUrls() {
        if (!lockRepository.tryAcquireLock(lockName)) {
            log.debug("Lock {} is already acquired", lockName);
            return;
        }

        List<String> expiredHashes = urlCleanupRepository.findExpiredHashes();

        if (!expiredHashes.isEmpty()) {
            log.debug("Cleaning up {} expired hashes", expiredHashes.size());
            urlCleanupRepository.deleteExpiredHashes(expiredHashes);
            urlCleanupRepository.saveHashesToPool(expiredHashes);
        }
    }
}
