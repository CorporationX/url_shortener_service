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

    @Scheduled(cron = "${url.cleaner.cron}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting cleaning job for URLs older than 1 year");

        List<String> freedHashes = urlRepository.deleteOldUrlsAndReturnHashes();

        if (!freedHashes.isEmpty()) {
            hashRepository.saveAll(freedHashes);
            log.info("Cleaned {} old URLs and reclaimed their hashes to the hash pool", freedHashes.size());
        } else {
            log.info("No old URLs found for cleanup");
        }
    }
}

