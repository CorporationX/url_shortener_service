package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${app.cleaner.batch-size:1000}")
    private int batchSize;

    @Value("${app.cleaner.ttl:P1Y}")
    private String ttl;

    @Scheduled(cron = "${app.cleaner.cron}")
    @Transactional
    public void cleanOldHashes() {
        Period period = Period.parse(ttl);
        LocalDateTime cutoff = LocalDateTime.now().minus(period);
        log.info("CleanerScheduler started: deleting URLs older than {}", cutoff);

        List<String> removedHashes = urlRepository.deleteOldHashes(cutoff);

        if (!removedHashes.isEmpty()) {
            log.info("CleanerScheduler: {} hashes to save", removedHashes.size());
            List<Hash> hashesToSave = removedHashes.stream()
                    .map(Hash::new)
                    .toList();
            saveInBatches(hashesToSave);
        } else {
            log.info("CleanerScheduler: no old URLs");
        }
    }

    private void saveInBatches(List<Hash> hashes) {
        for (int i = 0; i < hashes.size(); i += batchSize) {
            int toIndex = Math.min(i + batchSize, hashes.size());
            List<Hash> batch = hashes.subList(i, toIndex);
            hashRepository.saveAll(batch);
            log.debug("Saved batch of {} hashes", batch.size());
        }
    }
}


