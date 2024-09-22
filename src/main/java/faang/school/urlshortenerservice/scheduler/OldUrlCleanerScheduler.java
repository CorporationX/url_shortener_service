package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlShorterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class OldUrlCleanerScheduler {
    private final UrlShorterService urlShorterService;
    private final Period obsolescenceUrlPeriod;

    OldUrlCleanerScheduler(
            UrlShorterService urlShorterService,
            int obsolescenceUrlMonthPeriod
    ) {
        this.urlShorterService = urlShorterService;
        this.obsolescenceUrlPeriod = Period.ofMonths(obsolescenceUrlMonthPeriod);
    }

    @Scheduled(cron = "${spring.storage.url.old-cleaner-cron}")
    public void deleteOldUrls() {
        LocalDate obsolescenceDate = LocalDate.now().minus(obsolescenceUrlPeriod);
        urlShorterService.deleteOldULRs(obsolescenceDate);
    }
}
