package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;

    @Value("${hash.years_count}")
    private int yearsCount;

    @Scheduled(cron = "${hash.cron_clean}")
    public void clean() {
        urlService.reuseOldUrls(yearsCount);
    }
}