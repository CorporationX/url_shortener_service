package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepositoryImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepositoryImpl urlRepository;

    public CleanerScheduler(HashRepository hashRepository,
                            UrlRepositoryImpl urlRepository) {
        this.hashRepository = hashRepository;
        this.urlRepository = urlRepository;
    }

    @Scheduled(cron = "${hash.cache.cleaner.cron}")
    public void cleanUnusedHashes() {
        List<String> hashes = urlRepository.findExpiredHashes(LocalDateTime.now().minusYears(1));
        hashRepository.saveAll(hashes);
    }
}
