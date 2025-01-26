package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepositoryJdbc;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepositoryJdbc hashRepository;

    @Scheduled(cron = "${hash.cleaner.cron}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Starting job to clean old URLs...");

        try {
            List<String> freedHashes = urlRepository.deleteOldUrlsAndGetHashes();
            hashRepository.save(freedHashes);
            log.info("Job completed successfully. Freed hashes: {}", freedHashes.size());
        } catch (Exception ex) {
            log.error("Error occurred during cleaning job", ex);
            throw ex;
        }
    }
}
