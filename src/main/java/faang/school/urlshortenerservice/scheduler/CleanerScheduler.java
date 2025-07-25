package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.hash.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;
    private final HashRepository hashRepository;
    private final HashProperties hashProperties;

    @Scheduled(cron = "${cron.old_hash_cleaner}")
    @SchedulerLock(name = "${scheduler_names.hash_cleaner}")
    public void clean() {
        log.info("Old hashes cleaning started.");

        List<String> clearedHashes = urlService.retrieveOldUrls(hashProperties.daysBeforeClean());
        hashRepository.saveBatch(clearedHashes);

        log.info("Old hashes cleaning finished.");
    }
}
