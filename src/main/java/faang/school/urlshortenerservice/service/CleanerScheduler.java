package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashSaveRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final HashSaveRepository repository;
    private final UrlRepository urlRepository;

    @Transactional
    public void clear() {
        List<Hash> hashes = urlRepository.removeExpiredHashes();
        repository.save(hashes);
        log.info("Saved: {}", hashes);
    }

    @Scheduled(cron = "${cron}")
    public void hashClear() {
        clear();
        log.info("Hashes successfully removed");
    }
}