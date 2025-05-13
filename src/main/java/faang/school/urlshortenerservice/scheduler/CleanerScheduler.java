package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${shortener.cleaner.cron}")
    public void cleanOldUrls() {
        log.info("Starting clean old URLs");
        List<String> oldHashes = urlRepository.deleteOldAndReturnHashes();

        if (!oldHashes.isEmpty()) {
            hashRepository.saveAll(oldHashes);
            log.info("Returned {} hashes to pool", oldHashes.size());
        } else {
            log.info("No old URLs to clean");
        }
    }
}
