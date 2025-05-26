package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Value("${cleaner.scheduler.days-to-live}")
    private int daysToLive;

    @Scheduled(cron = "${cleaner.scheduler.cron}")
    public void cleanOldUrls() {
        Instant oneYearAgo = Instant.now().minus(daysToLive, ChronoUnit.DAYS);

        log.info("Starting cleaning old urls");

        List<String> expiredHashes = urlRepository.deleteByCreatedAtBefore(oneYearAgo);

        if (expiredHashes.isEmpty()) {
            log.info("There're no old URLs to clean");
        } else {
            log.info("{} old URLs found to recycle", expiredHashes.size());
            hashRepository.saveAllHashes(expiredHashes);
            log.info("Recycled {} hashes returned to the pool", expiredHashes.size());
        }
    }
}
