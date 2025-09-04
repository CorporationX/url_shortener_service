package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.cache.UrlCache;
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
    @Value("${shortener.hash.cleaner.after-days}")
    private int afterDays;
    @Value("${shortener.hash.cleaner.limit}")
    private int limit;

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCache urlCache;

    @Transactional
    @Scheduled(cron = "${shortener.hash.cleaner.cron}")
    public void cleanUpExpiredUrls() {
        List<String> strings = urlRepository.deleteUrlBeforeCreatedAt(
                LocalDateTime.now().minusDays(afterDays), limit
        );
        if (!strings.isEmpty()) {
            List<Hash> hashes = strings.stream()
                    .map(Hash::new)
                    .toList();
            hashRepository.saveAll(hashes);
            urlCache.deleteAll(strings);
        }
    }
}
