package faang.school.urlshortenerservice.Service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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

    private static final LocalDateTime ONE_YEAR_AGO = LocalDateTime.now().minusYears(1);

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.cleaner-cron}")
    public void cleanOldUrls() {
        log.info("Cleaning old urls was started");
        List<String> deletedHashes = urlRepository.deleteOlderThan(ONE_YEAR_AGO);
        log.info("removed from url: {} hashes", deletedHashes.size());
        if (!deletedHashes.isEmpty()) {
            hashRepository.saveHashesByBatch(deletedHashes);
            log.info("hashes saved in hash : {}", deletedHashes);
        }
        log.info("Cleaning old urls was finished");
    }
}
