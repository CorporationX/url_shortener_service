package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private static final String DELETE_NAME_TASK = "delete url older than 1 year from database.";
    private final UrlService urlService;

    @Scheduled(cron = "${cron.expression.clean_outdated_url}")
    public void cleanOutdatedUrls() {
        log.info("Task start: {}",DELETE_NAME_TASK);
        urlService.cleanOutdatedUrls();
        log.info("Task end: {}", DELETE_NAME_TASK);
    }
}
