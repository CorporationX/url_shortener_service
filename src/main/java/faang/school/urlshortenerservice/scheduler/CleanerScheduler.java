package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlService urlService;
    private final HashRepository hashRepository;

    @Value("${hash.days_before_clean}")
    private int daysCount;

    @Scheduled(cron = "${cron.old_hash_cleaner}")
    public void clean() {
        log.info("Old hashes cleaning started.");

        List<String> clearedHashes = urlService.retrieveOldUrls(daysCount);
        log.info("Found old hashes: {}.", clearedHashes);

        hashRepository.saveBatch(clearedHashes);

        log.info("Old hashes cleaning finished.");
    }
}
