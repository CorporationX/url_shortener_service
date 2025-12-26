package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.cleaner.cron}")
    public void cleanOldUrls() {
        log.info("Starting scheduled cleanup");
        List<String> hashes = urlRepository.deleteOldUrls();

        if (!hashes.isEmpty()) {
            hashRepository.save(hashes);
            log.info("Deleted {} URLs and returned hashes to pool", hashes.size());
        } else {
            log.info("No old URLs to delete");
        }
    }
}