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

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    @Value("${schedulers.cleaner.old-url-period.days}")
    private int oldUrlPeriodInDays;
    private final LocalDateTime removingDateTime = LocalDateTime.now().minusDays(oldUrlPeriodInDays);
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${schedulers.cleaner.cron}")
    public void scheduledCleaning() {
        cleanOldHashes();
    }

    @Transactional
    public void cleanOldHashes() {
        List<Hash> oldHashes = urlRepository.deleteOldUrl(removingDateTime).stream()
                .map(url -> new Hash(url.getHash()))
                .toList();
        hashRepository.saveAll(oldHashes);
        log.info("hash repository was cleaned at {}", LocalDateTime.now());
    }
}