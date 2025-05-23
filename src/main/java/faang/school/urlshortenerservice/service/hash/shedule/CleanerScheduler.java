package faang.school.urlshortenerservice.service.hash.shedule;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${hash.cleaner.cron.expression:0 0 0 * * *}")
    @Transactional
    public void cleanupOldUrlsAndRecycleHashes() {
        log.info("Starting cleanup of old URLs");
        List<String> recycledHashes = urlRepository.deleteUrlsOlderThanOneYearAndGetHashes();
        hashRepository.saveAll(recycledHashes);
        log.info("Cleanup completed successfully");
    }
}