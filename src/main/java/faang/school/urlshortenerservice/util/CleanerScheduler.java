package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${hash.url.cleaner.cron}")
    @Transactional
    public void urlCleaner() {
        log.info("Scheduled URL cleaner task started");

        List<String> hashes = urlRepository.removeOldLinksAndReturnHash();
        if (hashes.isEmpty()) {
            log.debug("No old URLs found to clean");
            return;
        }
        hashRepository.save(hashes);
        log.debug("Saved {} old hashes to the database", hashes.size());
        log.info("Scheduled URL cleaner task completed");
    }
}
