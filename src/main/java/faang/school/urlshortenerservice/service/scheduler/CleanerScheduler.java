package faang.school.urlshortenerservice.service.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${cleaner.cron}")
    public void cleanOldUrls() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(1);

        log.info("CleanerScheduler: Starting cleaning of URLs older than {}", threshold);
        List<String> freedHashes = urlRepository.deleteOldUrls(threshold);

        if (freedHashes.isEmpty()) {
            log.info("CleanerScheduler: No old URLs found for cleaning.");
            return;
        }

        List<Hash> hashes = freedHashes.stream()
                .map(hash -> new Hash(null, hash))
                .toList();

        hashRepository.saveAll(hashes);
        log.info("CleanerScheduler: Reinserted {} freed hashes into the hash table.", hashes.size());
    }
}