package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repo.HashRepository;
import faang.school.urlshortenerservice.repo.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${url.cleaner.cron:0 0 0 * * *}")
    @Transactional
    public void cleanOldUrlsAndRecycleHashes() {
        log.info("Starting cleaning old URLs job");

        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        try {
            List<String> recycledHashes = urlRepository.deleteOldUrlsAndReturnHashes(oneYearAgo);

            if (!recycledHashes.isEmpty()) {
                hashRepository.saveAllHashes(recycledHashes);
                log.info("Recycled {} hashes back to pool", recycledHashes.size());
            } else {
                log.info("No old URLs found to clean");
            }
        } catch (Exception e) {
            log.error("Failed to clean old URLs and recycle hashes", e);
            throw e;
        }
    }
}