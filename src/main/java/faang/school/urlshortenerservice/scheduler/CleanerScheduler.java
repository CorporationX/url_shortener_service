package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.manager.HashManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashManager hashManager;
    @Value("${hash.repository.scheduler.expirationRangeMonth}")
    private int expirationRangeMonth;

    @Scheduled(cron = "${hash.repository.scheduler.clear-cron}")
    public void clearHashes() {
        LocalDate expirationDate = LocalDate.now().minusMonths(expirationRangeMonth);
        hashManager.clearExpiredHashes(expirationDate);
    }
}
