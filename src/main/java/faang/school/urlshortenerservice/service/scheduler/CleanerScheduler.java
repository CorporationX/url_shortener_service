package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${server.hash.scheduler.cleaner.days-threshold}")
    private int daysThreshold;

    @Scheduled(cron = "${server.hash.scheduler.cleaner.cron}")
    @Transactional
    public void clearUnusedUrls() {
        LocalDate oneYearAgo = LocalDate.now().minusDays(daysThreshold);

        List<Hash> hashesToRecycle = urlRepository.deleteOldUrlsAndReturnHashes(oneYearAgo);

        if (!hashesToRecycle.isEmpty()) {
            hashRepository.saveBatch(hashesToRecycle);
        }
    }
}
