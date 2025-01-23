package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.CleanerSchedulerException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Планировщик для автоматической очистки старых URL из базы данных и сохранения их хэшей.
 * Запускается по расписанию, заданному через cron-выражение.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    /**
     * Очищает старые URL из базы данных и сохраняет их хэши в отдельном репозитории.
     * Запускается по расписанию, заданному через cron-выражение (по умолчанию каждый день в полночь).
     *
     * @throws CleanerSchedulerException Если произошла ошибка при удалении URL или сохранении хэшей.
     */
    @Transactional
    @Scheduled(cron = "${hash.cleaner.cron: 0 0 0 * * *}")
    public void cleanOldUrls() {
        log.info("Запуск планировщика для очистки старых URL...");

        try {
            List<String> deletedHashes = urlRepository.removeUrlOlderThanOneDay();
            log.info("Удалено {} старых URL", deletedHashes.size());

            List<Hash> hashes = deletedHashes.stream()
                .map(Hash::new)
                .toList();
            hashRepository.saveAll(hashes);
            log.info("Сохранено {} хэшей в hashRepository", hashes.size());

        } catch (Exception e) {
            log.error("Ошибка при очистке старых URL: {}", e.getMessage(), e);
            throw new CleanerSchedulerException("Ошибка при очистке старых URL: " + e.getMessage(), e);
        }
        log.info("Очистка старых URL завершена.");
    }
}
