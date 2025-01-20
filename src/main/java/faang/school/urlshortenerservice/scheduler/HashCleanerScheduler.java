package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Period;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashCleanerScheduler {
    private final static String RELEASED_HASH_MESSAGE = "{} hashes released!";
    private final UrlService urlService;

    @Value("${cleaning.period}")
    private String cleaningPeriod;

    @Transactional
    @Scheduled(cron = "${hash.cleaner.scheduled.cron}")
    public void clean() {
        Period period = Period.parse(cleaningPeriod);

        long releasedHashCount = urlService.cleanUrls(period);
        if (releasedHashCount > 0) {
            log.info(RELEASED_HASH_MESSAGE, releasedHashCount);
        }
    }
}
