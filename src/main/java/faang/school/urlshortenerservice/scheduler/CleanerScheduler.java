package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class CleanerScheduler {
    @Lazy private final CleanerScheduler cleanerScheduler;
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final UrlCacheRepository urlCacheRepository;

    @Scheduled(cron = "${cleaner_scheduler.free_old_hashes.cron}")
    public void freeOldHashes() {
        List<Hash> freeHashes = cleanerScheduler.freeOldHashesFromDb();
        urlCacheRepository.evictUrl(freeHashes);
    }

    @Transactional
    public List<Hash> freeOldHashesFromDb() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1L);
        List<Hash> hashes = urlRepository.deleteOldUrlsAndReturnFreeHashes(oneYearAgo)
                .stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
        return hashes;
    }
}
