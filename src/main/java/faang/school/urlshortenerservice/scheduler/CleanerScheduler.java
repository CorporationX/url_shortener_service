package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    @Value("${scheduler.cleaner-url-scheduler.parameters.months-from-now}")
    private int monthsFromNow;

    @Transactional
    @Scheduled(cron = "${scheduler.cleaner-url-scheduler.parameters.cron}")
    public void cleanUrl() {
        LocalDateTime dateFromNow = LocalDateTime.now().minusMonths(monthsFromNow);
        List<Hash> oldHashes = hashRepository.cleanAndGetHashes(dateFromNow);
        hashRepository.saveBatchHashes(oldHashes);
    }
}
