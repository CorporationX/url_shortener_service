package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Value("${app.life-links}")
    private Long lifeLinks;

    @Scheduled(cron = "${scheduler.cron}")
    public void cleanOldUrls() {
        log.info("Starting cleanup of old URLs");

        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(lifeLinks);
        List<String> freedHashes = urlRepository.deleteAllByCreatedAtBeforeReturningHashes(cutoffDate);

        List<Hash> hashEntities = freedHashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashEntities);
        freedHashes.forEach(urlCacheRepository::delete);
        log.info("Cleanup completed. Freed hashes: {}", freedHashes);
    }
}