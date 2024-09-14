package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;
    private final HashGenerator hashGenerator;

    @Transactional
    @Scheduled(cron = "${spring.task.scheduling.cron-time}")
    public void cleanOldHashes() {
        LocalDateTime oneYearRange = LocalDateTime.now().minusYears(1);
        List<String> hashes = urlService.deleteUrlByDate(oneYearRange);
        hashGenerator.saveHashes(hashes);
    }
}
