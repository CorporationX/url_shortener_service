package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
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
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Scheduled(cron = "${scheduler.cleanOldUrls}")
    @Transactional
    public void cleanOldUrlsAndSaveHash() {
        log.info("Start operation: Clean old url");
        List<String> hashes = urlRepository.deleteOldUrlsAndReturnHashes();
        hashRepository.saveAll(hashes.stream().map(Hash::new).toList());
    }
}
