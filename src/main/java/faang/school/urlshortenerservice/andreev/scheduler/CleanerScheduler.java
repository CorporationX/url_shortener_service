package faang.school.urlshortenerservice.andreev.scheduler;

import faang.school.urlshortenerservice.andreev.repository.HashRepository;
import faang.school.urlshortenerservice.andreev.repository.UrlRepository;
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

    @Scheduled(cron = "${scheduled.cleaner.cron:0 0 * * * *}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Start cleaning old urls");
        List<String> hashes = urlRepository.deleteUrlsOlderThanOneYear();

        if (!hashes.isEmpty()) {
            hashRepository.save(hashes);
            log.info("Returned {} hashes", hashes.size());
        } else {
            log.info("No old URLs to clean");
        }
    }
}
