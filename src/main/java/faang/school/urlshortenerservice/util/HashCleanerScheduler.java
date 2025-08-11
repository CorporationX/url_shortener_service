package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
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
public class HashCleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cronCleanHash}")
    @Transactional
    public void cleahHash() {
        log.info("Cleaning hash...");
        List<String> removedHashes = urlRepository.deleteAndReturnHashes();
        List<Hash> returningHashes = removedHashes.stream().map(hash -> new Hash(hash)).toList();
        hashRepository.saveAll(returningHashes);
        log.info("Hashes are cleaned.");
    }
}
