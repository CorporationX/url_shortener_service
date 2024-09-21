package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CleanerScheduler {
    private final int expirationDays;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    public CleanerScheduler(@Value("${url.expiration.days}") int expirationDays,
                            HashRepository hashRepository,
                            UrlRepository urlRepository) {
        this.expirationDays = expirationDays;
        this.hashRepository = hashRepository;
        this.urlRepository = urlRepository;
    }

    @Transactional
    @Scheduled(cron = "${url.expiration.cleanup.cron}")
    public void clean() {
        List<String> expiredHashes = urlRepository.deleteAndGetExpiredHashes(expirationDays);
        List<Hash> reusedHashes = expiredHashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(reusedHashes);
    }
}
