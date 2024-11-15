package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${server.hash.clear.cron}")
    public void clearUnusedHashes() {
        log.info("Clearing unused hashes");

        urlService.clearOutdatedUrls();

        log.info("Clearing unused hashes completed");
    }
}
