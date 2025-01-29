package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final int deleteLimit;

    public CleanerScheduler(
            UrlRepository urlRepository,
            HashRepository hashRepository,
            @Value("${hash-cleaning.limit}") int deleteLimit
    ) {
        this.urlRepository = urlRepository;
        this.hashRepository = hashRepository;
        this.deleteLimit = deleteLimit;
    }

    @Scheduled(cron = "${hash-cleaning.cron}")
    @Transactional
    public void cleanHashes() {
        List<String> cleanedHashes = urlRepository.deleteAndGetHashes(deleteLimit);
        List<Hash> hashes = cleanedHashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
    }
}
