package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;

@Slf4j
@Component
@EnableScheduling
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final String retentionPeriod;

    public CleanerScheduler(UrlRepository urlRepository,
                            HashRepository hashRepository,
                            @Value("${url.hash.cleaner.retention-period}") String retentionPeriod) {
        this.urlRepository = urlRepository;
        this.hashRepository = hashRepository;
        this.retentionPeriod = retentionPeriod;
    }

    @Transactional
    @Scheduled(cron = "${url.hash.cleaner.cron}")
    public void cleanUpOutdatedUrls() {
        log.info("Clean up of outdated URLs started...");
        var oneYearAgo = LocalDateTime.now().minus(Period.parse(retentionPeriod));
        var freedHashes = urlRepository.deleteUrlsAndFreeHashes(oneYearAgo);
        if (!freedHashes.isEmpty()) {
            hashRepository.save(freedHashes);
        }
        log.info("Cleanup completed. Freed up {} hashes from outdated URLs", freedHashes.size());
    }
}
