package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashDao;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlCleanupTask {
    private final UrlRepository urlRepository;
    private final HashDao hashDao;

    @Value("${url.cleanup.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${url.cleanup.cron}")
    @Transactional
    public void cleanupExpiredUrls() {
        log.info("Starting expired URL cleanup job.");
        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(1);
        Pageable pageable = PageRequest.of(0, batchSize);
        int totalProcessed = 0;
        Slice<Url> slice;

        do {
            slice = urlRepository.findByCreatedAtBefore(cutoffDate, pageable);
            List<Url> expiredUrls = slice.getContent();
            if (expiredUrls.isEmpty()) {
                break;
            }
            log.info("Found a batch of {} expired URLs to process.", expiredUrls.size());
            List<String> hashesToReturn = expiredUrls.stream()
                    .map(Url::getHash)
                    .toList();
            hashDao.save(hashesToReturn);
            urlRepository.deleteAllInBatch(expiredUrls);
            log.info("Processed batch: Returned {} hashes and deleted {} URLs.",
                    hashesToReturn.size(), expiredUrls.size());
            totalProcessed += expiredUrls.size();
            pageable = slice.nextPageable();
        } while (slice.hasNext());

        if (totalProcessed > 0) {
            log.info("Expired URL cleanup job finished. Total URLs processed: {}", totalProcessed);
        } else {
            log.info("Expired URL cleanup job finished. No expired URLs were found.");
        }
    }
}
