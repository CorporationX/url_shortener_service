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

    @Scheduled(cron = "${scheduler.old_url_hashes}")
    public void cleanOldUrlHashes() {
        List<String> deletedHashes = urlService.cleanOldUrls();
        System.out.println("Deleted hashes of old URL: " + deletedHashes);
    }
}
