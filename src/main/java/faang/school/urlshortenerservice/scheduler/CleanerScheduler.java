package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Async("schedulerCustom")
    @Scheduled(cron = "${scheduler-setting.clean-unused-associations}")
    public void cleanUnusedAssociations() {
        log.debug("Cleaning unused associations started at {}", LocalDateTime.now());
        urlService.cleanUnusedAssociations();
        log.debug("Cleaning unused associations finished at {}", LocalDateTime.now());
    }
}
