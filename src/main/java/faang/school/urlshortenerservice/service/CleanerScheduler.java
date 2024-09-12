package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final int expirationYears;

    public CleanerScheduler(UrlRepository urlRepository,
                            HashRepository hashRepository,
                            @Value("${data.hash.cleaner.expired_after_years}") int expirationYears) {
        this.urlRepository = urlRepository;
        this.hashRepository = hashRepository;
        this.expirationYears = expirationYears;
    }

    @Scheduled(cron = "${data.hash.cleaner.cron:0 0 0 * * *}")
    public void clean() {
        LocalDateTime dateExpired = LocalDateTime.now().minusYears(expirationYears);
        List<Hash> hashesExpired = urlRepository.findAndDeleteHashExpired(dateExpired).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashesExpired);
    }

}
