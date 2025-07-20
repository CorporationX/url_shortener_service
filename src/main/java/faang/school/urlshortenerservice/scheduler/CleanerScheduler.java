package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlServiceImpl;
    @Value("${hashes.expired.delete.batch}")
    private int batchLimit;
    @Value("${hashes.expired.cutoff-days}")
    private int cutoffDays;

    @Scheduled(cron = "${scheduler.daily-cron}")
    public void cleanExpiredHashes() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(cutoffDays);
            List<String> expiredHashesBatch;
            do {
                expiredHashesBatch = urlServiceImpl.deleteOldReturningHashes(cutoff, batchLimit);
                log.info("Clean hashes batch completed, removed {} expired hashes", expiredHashesBatch.size());
            } while (!expiredHashesBatch.isEmpty());
            log.info("Clean hashes process fully completed");
        } catch (Exception e) {
            log.error("Error during hash cleanup", e);
            throw e;
        }
    }
}
