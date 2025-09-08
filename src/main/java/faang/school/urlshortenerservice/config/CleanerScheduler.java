package faang.school.urlshortenerservice.config;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    @Value("${hash.cleaner.after-days}")
    private int afterDays;

    @Transactional
    @Scheduled(cron = "${hash.cleaner.cron}")
    void cleanUpExpiredUrls() {
        List<Hash> hashes = urlRepository.deleteUrlBeforeCreatedAt(
                        LocalDateTime.now().minusDays(afterDays)
                ).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }
}