package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;
    private final HashService hashService;

    @Value("${batch.size}")
    private Integer BATCH_SIZE;

    @Transactional
    @Scheduled(cron = "${scheduler.cleaner.cron}")
    public void cleanOldUrls() {
        log.info("Starting cleanOldUrls job...");

        try {
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

            List<String> freedHashes = urlService.deleteOldUrlsAndGetHashes(oneMonthAgo);
            log.info("Total freed hashes: {}", freedHashes.size());

            for (int i = 0; i < freedHashes.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, freedHashes.size());
                List<String> batch = freedHashes.subList(i, end);

                hashService.saveUnusedHashes(batch);
                log.info("Processed batch {} to {} of size {}", i, end, batch.size());
            }

            log.info("Job cleanOldUrls completed successfully. Freed hashes: {}", freedHashes.size());
        } catch (Exception e) {
            log.error("Error during cleanOldUrls job", e);
            throw e;
        }
    }
}
