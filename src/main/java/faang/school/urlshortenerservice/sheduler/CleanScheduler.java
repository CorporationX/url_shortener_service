package faang.school.urlshortenerservice.sheduler;

import faang.school.urlshortenerservice.service.UrlCleanerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanScheduler {
    private final UrlCleanerService urlCleanerService;

    @Scheduled(cron = "${cleaner-scheduler.cron}")
    public void cleanOldUrls() {
        urlCleanerService.deleteOlderUrlsByTtL();
    }
}
