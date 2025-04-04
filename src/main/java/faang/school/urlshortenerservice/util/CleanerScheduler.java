package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.service.HashCleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final HashCleanerService hashCleanerService;

    @Scheduled(cron = "${schedulers.config.cleanupOutdatedHashes.cronExpression}")
    public void cleanupOutdatedHashes () {
        hashCleanerService.cleanupOutdatedHashes();
    }
}
