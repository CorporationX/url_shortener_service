package faang.school.urlshortenerservice.cleaner;


import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    @Scheduled(cron = "${hash.scheduled.cron}")
    public void clean() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<String> freedHashes = urlRepository.deleteOldUrlsAndReturnHashes(oneYearAgo);
        if (!freedHashes.isEmpty()) {
            List<Hash> hashEntities = freedHashes.stream()
                    .map(Hash::new)
                    .toList();
            hashRepository.saveAll(hashEntities);
            log.info("Successfully cleaned old URLs and freed {} hashes.", freedHashes.size());
        } else {
            log.info("No old URLs to clean");
        }
    }
}
