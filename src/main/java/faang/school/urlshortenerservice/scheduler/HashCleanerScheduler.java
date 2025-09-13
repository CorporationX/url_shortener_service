package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${hash.cleaner.release-after-days}")
    private int expireAfterDays;

    @Value("${hash.cleaner.save-batch-size}")
    private int saveBatchSize;

    @Scheduled(cron = "${hash.cleaner.cron}")
    public void releaseOldHashes(){
        List<String> oldHashes = urlRepository.getHashesOlderThanAndDelete(expireAfterDays);
        if (!oldHashes.isEmpty()) {
            log.info("Releasing {} expired hashes.", oldHashes.size());
            hashRepository.saveAllBatched(oldHashes.stream().map(Hash::new).toList(), saveBatchSize);
        }
    }
}
