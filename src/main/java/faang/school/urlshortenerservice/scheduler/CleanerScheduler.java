package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${url.cleaner.scheduler.cron}")
    @Transactional
    public void cleanExpiredUrls() {
        log.info("Starting clean expired urls");
        List<String> freeHashes = urlRepository.deleteOldUrlsAndGetReleasedHashes(LocalDateTime.now().minusYears(1));
        if (!CollectionUtils.isEmpty(freeHashes)) {
            hashRepository.saveAll(freeHashes);
            log.info("Saved array of hashes with size: {}", freeHashes.size());
        }
    }
}


