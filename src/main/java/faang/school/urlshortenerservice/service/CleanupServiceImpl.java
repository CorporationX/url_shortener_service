package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.CleanupException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CleanupServiceImpl implements CleanupService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> deleteExpiredBatch(LocalDateTime expiryDate, int batchSize) {
        return urlRepository.deleteExpiredUrlsBatch(expiryDate, batchSize);
    }

    @Override
    @Transactional
    public void returnHashesToPool(List<String> hashes) {
        if (hashes.isEmpty()) return;

        log.debug("Returning {} hashes back to pool", hashes.size());
        try {
            hashRepository.saveBatch(hashes);
            log.debug("Successfully returned {} hashes to pool", hashes.size());
        } catch (Exception e) {
            log.error("Failed to return {} hashes to pool", hashes.size(), e);
            throw new CleanupException("Failed to return hashes to pool", e);
        }
    }
}
