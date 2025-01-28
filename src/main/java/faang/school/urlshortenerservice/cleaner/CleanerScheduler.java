package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    @Value("${scheduler.year}")
    private int year;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${scheduler.cron}")
    @Transactional
    public void removeOldUrls() {
        List<String> oldHashes = urlRepository.removeOldLinks(LocalDateTime.now().minusYears(year));

        if (oldHashes.isEmpty()) {
            return;
        }

        hashRepository.saveAll(oldHashes.stream()
                .map(Hash::new)
                .toList());
    }
}
