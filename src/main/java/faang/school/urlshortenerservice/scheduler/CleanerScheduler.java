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
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Value("${scheduler.cleaner-expiration-months}")
    private int expirationPeriodInMonths;

    @Scheduled(cron = "${scheduler.cleaner-start-cron-expression}")
    @Transactional
    public void deleteExpiredUrlsAndRecycleHashes() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMonths(expirationPeriodInMonths);
        List<Hash> recycledHashes = urlRepository.deleteHashesEarlierThan(expirationThreshold);
        hashRepository.saveAll(recycledHashes);
        log.info("Deleted {} expired URLs and recycled {} hashes", recycledHashes.size(), recycledHashes.size());
    }
}
