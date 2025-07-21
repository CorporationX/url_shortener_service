package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.cleaner.batch-size}")
    private int batchSize;

    @Value("${app.cleaner.age-interval}")
    private String ageIntervalSql;

    @Transactional
    @Scheduled(cron = "${app.scheduler.cleaner-cron}")
    public void cleanOldUrls() {
        List<String> hashes;
        do {
            hashes = urlRepository.deleteOldReturningHashes(ageIntervalSql, batchSize);
            if (!hashes.isEmpty()) {

                hashRepository.saveAll(
                        hashes.stream()
                                .map(Hash::new)
                                .toList()
                );
            }
        } while (hashes.size() == batchSize);
    }
}
