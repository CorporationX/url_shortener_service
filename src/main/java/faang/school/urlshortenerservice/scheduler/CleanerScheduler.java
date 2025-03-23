package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;

    @Value("${schedule.retention-period}")
    private Duration retentionPeriod;

    @Scheduled(cron = "${schedule.cleaner-cron}")
    public void cleanOldUrlsScheduled() {
        cleanOldUrls();
    }

    @Transactional
    public void cleanOldUrls() {
        log.info("Запуск задачи очистки старых URL...");
        LocalDateTime cutoffDate = LocalDateTime.now().minus(retentionPeriod);
        urlService.removeOldUrls(cutoffDate);
        log.info("Очистка старых URL завершена.");
    }
}

