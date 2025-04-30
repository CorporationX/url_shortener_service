package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.exception.SchedulerException;
import faang.school.urlshortenerservice.service.url.UrlCleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class UrlScheduler {
    private final UrlCleanerService urlCleanerService;

    @Value("${url.scheduler.ttl-days}")
    private int urlTtlDays;

    @Async("taskExecutor")
    @Scheduled(cron = "${url.scheduler.clean-cron}")
    public void cleanOldUrls() {
        LocalDateTime expirationDateTime = LocalDateTime.now().minusDays(urlTtlDays);

        try {
            urlCleanerService.cleanOldUrls(expirationDateTime);
            log.info("Successfully cleaned old URLs.");
        } catch (Exception e) {
            log.error("Error occurred while cleaning old URLs: {}", e.getMessage(), e);
            throw new SchedulerException(e.getMessage());
        }
    }
}
