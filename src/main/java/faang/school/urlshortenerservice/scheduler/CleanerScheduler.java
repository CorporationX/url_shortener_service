package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(cron = "${scheduler.cron.expression}")
    public void cleanupOldUrl() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        transactionTemplate.execute(status -> {
            List<String> hashes = urlRepository.deleteOldUrls(oneYearAgo);
            hashRepository.saveBatch(hashes);

            return null;
        });
    }
}
