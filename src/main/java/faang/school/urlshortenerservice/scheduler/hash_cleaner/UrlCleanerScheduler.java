package faang.school.urlshortenerservice.scheduler.hash_cleaner;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCleanerScheduler {

    private final UrlService urlService;

    @Async("urlHashTaskExecutor")
    @Scheduled(cron = "${scheduler.clean-old-urls.cron}")
    public void moveOldHashesToFreeHashes() {
        urlService.deleteOldUrls();
    }
}