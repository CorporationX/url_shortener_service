package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.URL;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.URLRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class URLScheduler {
    private final URLRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${retention.days:365}")
    private int retentionDays;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldURLs() {
        long startTime = System.currentTimeMillis();

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
        List<URL> oldUrls = urlRepository.findOldURLs(cutoffDate);

        if (oldUrls.isEmpty()) {
            log.info("No old URLs to clean up");
            return;
        }

        List<String> hashesToRecycle = new ArrayList<>(oldUrls.size());
        for (URL url : oldUrls) {
            hashesToRecycle.add(url.getHash());
        }

        List<Hash> hashesToSave = new ArrayList<>(oldUrls.size());
        for (String hashValue : hashesToRecycle) {
            hashesToSave.add(Hash.builder()
                    .hashValue(hashValue)
                    .build());
        }

        int batchSize = 1000;
        for (int i = 0; i < hashesToSave.size(); i += batchSize) {
            int end = Math.min(i + batchSize, hashesToSave.size());
            hashRepository.saveAll(hashesToSave.subList(i, end));
        }

        urlRepository.deleteAll(oldUrls);

        long duration = System.currentTimeMillis() - startTime;
        log.info("Cleaned up {} old URLs and recycled their hashes in {} ms",
                oldUrls.size(), duration);
    }
}
