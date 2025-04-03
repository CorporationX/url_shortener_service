package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlServiceImpl urlServiceImpl;

    @Scheduled(cron = "${cron.url-cleaner}")
    public void cleanOldUrls() {
        log.info("Starting cleaning old URLs");
        urlServiceImpl.cleaningExpiredUrls();
        log.info("Finished cleaning old URLs");
    }
}
