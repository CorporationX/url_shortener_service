package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlCacheService;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlService urlService;
    private final UrlCacheService urlCacheService;

    @Scheduled(cron = "${cleaner.cron.cleanTime:0 0 0 * * ?}")
    @SchedulerLock(name = "CleanerScheduler_cleanOldUrls",
            lockAtLeastFor = "PT2M", lockAtMostFor = "PT10M")
    public void cleanOldUrls() {
        log.info("Starting cleaning of obsolete URLs...");
        urlService.deleteOldUrls();
    }
}
