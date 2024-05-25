package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CleanerScheduler {

    private final HashService hashService;

    @Async("asyncExecutor")
    @Scheduled(cron = "${app.scheduler.cleaner}")
    public void cleanAndMoveHashes() {
        hashService.clean();
    }

}
