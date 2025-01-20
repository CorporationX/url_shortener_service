package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${hash.cron.clean-up}")
    @Transactional
    public void cleanExpiredUrls() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minus(1, ChronoUnit.YEARS);
        List<Hash> expiredHashes = urlRepository.deleteExpiredUrlsAndReturnHashes(oneYearAgo);
        if (!expiredHashes.isEmpty()) {
            hashRepository.saveAll(expiredHashes);
        }
    }
}
