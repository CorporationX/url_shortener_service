package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final HashGenerator hashGenerator;
    private final UrlService urlService;

    @Value("${cleanup.period}")
    private String cleanupPeriod;

    @Transactional
    @Scheduled(cron = "${hash.scheduled.cron}")
    public void clean() {
        Period period = Period.parse(cleanupPeriod);

        int freedHashedCount = urlService.cleanOldUrls(period);
        if (freedHashedCount > 0) {
            hashGenerator.generateAndSaveBatch(freedHashedCount);
        } else {
            log.info("No old URLs cleaned, skipping hash generation.");
        }
    }
}
