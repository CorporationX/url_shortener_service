package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${deleting_old_url.storage_life_in_days:365}")
    private int storageLifeInDays;

    @Scheduled(cron = "${deleting_old_url.cron:0 0 4 * * *}")
    @Transactional
    public void deleteOldUrls() {
        LocalDateTime storageLife = LocalDateTime.now().minusDays(storageLifeInDays);
        log.info("Scheduled deleting old urls starts.");
        List<String> liberatedHashes = urlRepository.getAndDeleteOldHashes(storageLife).stream()
                .map(Url::getHash)
                .toList();
        if (liberatedHashes.isEmpty()) {
            log.info("Old hashes din not find. Scheduled deleting old urls finished.");
            return;
        }
        log.debug("{} hashes were liberated.", liberatedHashes.size());
        urlCacheRepository.deleteAllById(liberatedHashes);
        log.debug("{} hashes were deleted from cache.", liberatedHashes.size());
        List<Hash> freeHashes = liberatedHashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(freeHashes);
        log.info("Scheduled deleting old urls finished, {} hashes were inserted into DB with free hashes.",
                freeHashes.size());
    }
}
