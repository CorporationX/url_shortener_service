package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    @Value("${schedulers.cleaner-scheduler.years-ago-to-delete-hashes:1}")
    private int yearsAgoToDeleteHashes;

    private final UrlService urlService;

    @Scheduled(cron = "${schedulers.cleaner-scheduler.cron}")
    public void cleanExpiredHashes() {
        urlService.cleanExpiredHashes(yearsAgoToDeleteHashes);
    }
}