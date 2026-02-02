package faang.school.urlshortenerservice.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.repository.UrlJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlJdbcRepository urlJdbcRepository;
    private final HashJdbcRepository hashJdbcRepository;

    @Scheduled(cron = "${cleaner.cron}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting cleanup job for old URLs");

        List<String> freedHashes = urlJdbcRepository.deleteOldUrlsAndReturnHashes();

        if (freedHashes.isEmpty()) {
            log.info("No old URLs found to clean");
            return;
        }

        log.info("Found {} old URLs to clean, freeing {} hashes", freedHashes.size(), freedHashes.size());
        
        hashJdbcRepository.save(freedHashes);
        
        log.info("Successfully cleaned {} old URLs and returned {} hashes to hash table", 
                freedHashes.size(), freedHashes.size());
    }
}

