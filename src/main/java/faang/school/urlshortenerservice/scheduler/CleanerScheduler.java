package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.entity.Hash;
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

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cron.cleaner}", zone = "UTC")
    @Transactional
    public void cleanOldUrls() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        try {
            List<String> hashes = urlRepository.findHashesOfUrlsOlderThan(oneYearAgo);

            if (!hashes.isEmpty()) {
                hashes.forEach(hash -> {
                    Hash hashEntity = new Hash(hash);
                    hashRepository.save(hashEntity);
                });
            }

            urlRepository.deleteUrlsOlderThan(oneYearAgo);
            log.info("The old associations have been deleted, and the hashes have been moved to the hash table.");
        } catch (Exception e) {
            log.error("Error while deleting old records and saving hashes.", e);
        }
    }
}