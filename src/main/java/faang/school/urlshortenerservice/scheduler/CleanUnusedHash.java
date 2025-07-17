package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.url.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CleanUnusedHash {

    private final UrlService urlService;

    @Value("${scheduler.clean-unused-hash.daily-cron}")
    private String dailyCron;

    @Scheduled(cron = "${scheduler.clean-unused-hash.daily-cron}")
    public void cleanUnusedHash() {
        log.info("Starting scheduled task to clean unused hashes");
        urlService.cleanUnusedHash();
    }
}
