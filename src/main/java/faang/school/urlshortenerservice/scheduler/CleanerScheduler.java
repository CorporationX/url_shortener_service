package faang.school.urlshortenerservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    @Scheduled(cron = "${clean.cron}")
    @Transactional
    public void cleanExpiredUrls() {
        log.info("Starting clean expired urls");
    }
}


