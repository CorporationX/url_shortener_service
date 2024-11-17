package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;

    @Scheduled(cron = "${scheduler.daily}")
    public void cleanAssociations() {
        List<String> deletedHashes = urlService.cleanOldUrls();
        System.out.println("Удалены хэши старых URL: " + deletedHashes);
    }
}
