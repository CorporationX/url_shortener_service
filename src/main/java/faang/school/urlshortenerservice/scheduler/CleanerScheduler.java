package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
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
    private final UrlCacheRepository urlCacheRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${url.cleaner.cron:0 0 0 * * *}")
    @Transactional
    public void clean() {
        List<String> deletedHashes = urlRepository.deleteAndReturnExpiredHashes();
        urlCacheRepository.deleteBatch(deletedHashes);
        if (!deletedHashes.isEmpty()) {
            hashRepository.saveBatch(deletedHashes);
        }
        log.info("Expired hashes have been deleted: {}", deletedHashes);
    }
}
