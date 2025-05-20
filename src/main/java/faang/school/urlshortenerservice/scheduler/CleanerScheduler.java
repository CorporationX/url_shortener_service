package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${app.cleaner.cron}")
    @Transactional
    public void cleanOldHashes() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        log.info("CleanerScheduler started: deleting URLs older than {}", oneYearAgo);

        List<String> removedHashes = urlRepository.deleteOldHashes(oneYearAgo);

        if (!removedHashes.isEmpty()) {
            List<Hash> hashesToSave = removedHashes.stream()
                    .map(Hash::new)
                    .toList();

            hashRepository.saveAll(hashesToSave);
            log.info("CleanerScheduler: saved {} hashes back to hash table", hashesToSave.size());
        } else {
            log.info("CleanerScheduler: no old URLs");
        }
    }
}
