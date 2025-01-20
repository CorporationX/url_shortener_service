package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.CleanHashesService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final CleanHashesService cleanHashesService;

    @Scheduled(cron = "${scheduler.clean-hashes.cron}")
    public void startEvent() {
        cleanHashesService.cleanHashes();
    }
}
