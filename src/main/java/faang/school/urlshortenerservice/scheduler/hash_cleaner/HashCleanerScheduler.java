package faang.school.urlshortenerservice.scheduler.hash_cleaner;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${scheduler.clean-old-hashes.ttl-months}")
    private int oldHashesTtlMonths;

    @Transactional
    @Async("urlHashTaskExecutor")
    @Scheduled(cron = "${scheduler.clean-old-hashes.cron}")
    public void moveOldHashesToFreeHashes() {
        log.info("Starting removing old hashes and moving them to free hashes");
        LocalDateTime oldHashesThresholdDate = LocalDateTime.now().minusMonths(oldHashesTtlMonths);
        List<String> oldHashes = urlRepository.deleteOldUrls(oldHashesThresholdDate);
        hashRepository.save(oldHashes);
        log.info("Finished removing old hashes and moving them to free hashes: {}", oldHashes);
    }
}