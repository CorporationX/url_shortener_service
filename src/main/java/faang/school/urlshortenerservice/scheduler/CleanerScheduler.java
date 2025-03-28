package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.CleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CleanerScheduler {

    private final CleanerService cleanerService;

    @Scheduled(cron = "${hash.scheduler.cleanOldUrls.fixed-rate}")
    public void cleanOldUrls() {
        cleanerService.cleanOldUrlsAndSaveHashes();
    }
}
