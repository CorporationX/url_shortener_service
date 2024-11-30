package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.CleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final CleanerService cleanerService;

    @Scheduled(cron = "${cleaner.cron}")
    public void clean() {
        cleanerService.clean();
    }
}
