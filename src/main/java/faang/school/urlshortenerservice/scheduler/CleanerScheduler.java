package faang.school.urlshortenerservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlCleaningService cleaningService;

    @Scheduled(cron = "${app.scheduler.cleaner-cron}")
    public void cleanOldUrls() {
        List<String> hashes;
        do {
            hashes = cleaningService.cleanOneBatch();
            log.info("Removed {} URLs in this batch", hashes.size());
        } while (hashes.size() == cleaningService.getBatchSize());
    }
}
