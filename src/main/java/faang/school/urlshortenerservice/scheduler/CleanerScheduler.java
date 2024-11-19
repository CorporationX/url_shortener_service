package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${cleaner.scheduler.cron}")
    @Transactional
    public void cleaner() {
        List<String> oldHashes = urlRepository.retrieveOldHashes();
        log.info("The {} hashes older than 1 year have been deleted", oldHashes.size());
        List<Hash> hashes = oldHashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }
}
