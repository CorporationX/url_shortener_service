package faang.school.urlshortenerservice.job;

import faang.school.urlshortenerservice.service.HashCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final HashCleaner cleaner;

    @Scheduled(cron = "${cron_expression}")
    public void hashClear() {
        cleaner.hashClear();
        log.info("Hashes was deleted successfully");
    }
}
