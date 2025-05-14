package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
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
    private final UrlCacheRepository urlCacheRepository;
    private final HashService hashService;

    @Transactional
    @Scheduled(cron = "${hash-generator.cron-delete}")
    public void deleteExpiredUrls() {
        log.info("Starting cleanup of expired URLs");
        List<String> deletedHashes = urlRepository.deleteExpiredUrls();
        deletedHashes.forEach(urlCacheRepository::removeUrl);
        hashService.saveFreeHashes(deletedHashes);
        log.info("Cleaned up {} expired URLs", deletedHashes.size());
    }
}