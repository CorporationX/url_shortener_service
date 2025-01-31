package faang.school.urlshortenerservice.schedule;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    @Value("${scheduler.year}")
    private int year;
    @Value("${batch-size.size}")
    private int batchSize;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cron}")
    public void cleanOldUrls() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(year);

        List<String> hashes = urlRepository.deleteUrlsOlderThan(oneYearAgo);

        List<Hash> batch = new ArrayList<>(batchSize);

        for (String hash : hashes) {
            batch.add(new Hash(hash));
        }

        if (batch.size() == batchSize) {
            saveBatch(batch);
            batch.clear();
        }

        if (!batch.isEmpty()) {
            saveBatch(batch);
        }

        log.info("Deleted URLs older than {} years", year);
    }

    private void saveBatch(List<Hash> batch) {
        try {
            hashRepository.saveAll(batch);
        } catch (Exception e) {
            log.error("Failed to save batch: {}", batch, e);
            throw e;
        }
    }
}
