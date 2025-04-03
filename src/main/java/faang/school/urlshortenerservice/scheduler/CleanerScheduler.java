package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlCleaningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlCleaningService urlCleaningService;

    @Scheduled(cron = "${scheduler.cleaner.cron}")
    public void cleanOldAssociations() {
        urlCleaningService.cleanAndSaveHashes();
    }
}
