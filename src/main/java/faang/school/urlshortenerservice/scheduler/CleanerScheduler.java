package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashService hashService;

    @Scheduled(cron = "${app.scheduler.cleaner}")
    @Async("hashExecutor")
    public void cleanOnSchedule() {
        hashService.clean();
    }
}
