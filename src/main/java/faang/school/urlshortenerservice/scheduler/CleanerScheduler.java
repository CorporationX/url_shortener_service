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
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    @Value("${scheduler.cleaning.url.expiration-interval}")
    private int expirationInterval;

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${scheduler.cleaning.url.cron}")
    public void removingExpiredUrlsAndSavingHashes() {
        log.info("Started job removingExpiredUrlsAndSavingHashes in " + CleanerScheduler.class);
        realizationJob();
    }

    @Transactional
    private void realizationJob() {
        LocalDateTime cutoffDate = LocalDateTime.now().minus(expirationInterval, ChronoUnit.YEARS);
        List<String> stringHashes = urlRepository.findExpiredUrls(cutoffDate);
        List<Hash> hashes = stringHashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
        urlRepository.deleteExpiredUrls();
    }

}
