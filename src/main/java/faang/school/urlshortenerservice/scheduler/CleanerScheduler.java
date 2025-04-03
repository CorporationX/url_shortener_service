package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
@EnableScheduling
@Configuration
public class CleanerScheduler {
    private final MaintenanceService maintenanceService;

    @Scheduled(cron = "${hash.cleaner.cron}")
    public void cleanUpOldUrls() {
        maintenanceService.cleanUpOldUrls();
    }
}
