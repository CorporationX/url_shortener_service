package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
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

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    /**
     * Удаляет старые URL и возвращает их хэши в таблицу hash
     * Запускается раз в день по cron из конфигурации
     */
    @Scheduled(cron = "${cleaner.cron:0 0 2 * * *}")
    public void cleanOldUrls() {
        log.info("Starting cleanup of old URLs");

        try {
            List<String> freedHashes = urlRepository.deleteOldUrlsAndReturnHashes();

            if (freedHashes.isEmpty()) {
                log.info("No old URLs found to clean up");
                return;
            }

            log.info("Deleted {} old URLs", freedHashes.size());

            hashRepository.save(freedHashes);

            log.info("Successfully cleaned up {} old URLs and returned hashes to pool", freedHashes.size());

        } catch (Exception e) {
            log.error("Error during URL cleanup", e);
            throw e;
        }
    }
}