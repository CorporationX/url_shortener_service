package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.entity.hash.HashEntity;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Scheduled(cron = "${cleaner.cron}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting cleanup job");
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        
        List<String> oldHashes = urlRepository.deleteOldUrlsAndReturnHashes(oneYearAgo);
        
        if (!oldHashes.isEmpty()) {
            List<HashEntity> hashEntities = oldHashes.stream()
                .map(HashEntity::new)
                .collect(Collectors.toList());
            
            hashRepository.saveAll(hashEntities);
            log.info("Cleaned up {} old URLs and moved their hashes to hash table", oldHashes.size());
        } else {
            log.info("No old URLs to clean up");
        }
    }
}