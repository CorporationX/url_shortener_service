package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

    @Value("${hash-cleaner.days-passed-for-remove}")
    private int daysPassedForRemove;

    @Async("cleanerSchedulerThreadPool")
    @Transactional
    @Scheduled(cron = "${hash-cleaner.cron}", zone = "${hash-cleaner.zone}")
    public void freeHashes() {
        log.info("Started freeing hashes");
        List<String> removedHashes = urlRepository.removeOld(LocalDateTime.now().minusDays(daysPassedForRemove));
        hashRepository.save(removedHashes);
        log.info("Freed {} hashes", removedHashes.size());
    }
}
