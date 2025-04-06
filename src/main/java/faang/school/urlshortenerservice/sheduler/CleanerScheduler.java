package faang.school.urlshortenerservice.sheduler;

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

    @Scheduled(cron = "${url.cleaner-cron}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting CleanerScheduler job to delete old URLs and reclaim hashes");

        try {
            List<String> freedHashes  = urlRepository.deleteOldUrlsAndReturnHashes();

            if (!freedHashes.isEmpty()) {
                hashRepository.save(freedHashes);
                log.info("Reclaimed {} hashes and saved them back to the hash table.", freedHashes.size());
            } else {
                log.info("No old URLs were found for deletion");
            }

        } catch (Exception e) {
            log.error("Error occurred while cleaning old URLs: ", e);
        }
    }
}