package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
@RequiredArgsConstructor
public class CleanerScheduler  {
    private final UrlService urlService;

    @Async
    @Scheduled(cron = "${spring.properties.cron}")
    public void remove() {
        try {
            log.info("Starting to remove expired urls and add them to Hash table");
            urlService.removeExpiredAndAddToHashRepo();
        } catch (Exception e) {
            log.error("Unable to remove urls", e);
            throw new RuntimeException(e);
        }
    }
}
