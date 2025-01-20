package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.properties.CleanerSchedulerProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {

    private final CleanerSchedulerProperties cleanerSchedulerProperties;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "#{cleanerSchedulerProperties.cron}")
    @Transactional
    public void cleanUp() {
        log.info("Cleaner job started");

        List<String> freedHashes = urlRepository.deleteOlderThanOneYear();

        if (!freedHashes.isEmpty()) {
            hashRepository.save(freedHashes);
            log.info("Cleaner job: freed {} hashes", freedHashes.size());
        } else {
            log.info("Cleaner job: no old records found");
        }
    }
}

