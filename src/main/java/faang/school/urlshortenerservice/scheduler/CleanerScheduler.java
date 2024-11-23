package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlService urlService;

    @Value("${server.hash.scheduler.cleaner.days-threshold}")
    private int daysThreshold;

    @Scheduled(cron = "${server.hash.scheduler.cleaner.cron}")
    public void cleaningOldHashes() {
        log.info("Clearing old hashes");

        LocalDate oneYearAgo = LocalDate.now().minusDays(daysThreshold);
        urlService.cleaningOldHashes(oneYearAgo);

        log.info("Clearing unused hashes completed");
    }
}
