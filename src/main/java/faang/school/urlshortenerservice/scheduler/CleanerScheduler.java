package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.config.SchedulerProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final SchedulerProperties schedulerProperties;

    @Scheduled(cron = "${scheduler.cleaner.cron:0 0 3 * * *}")
    @Transactional
    public void cleanOldUrlsAndRecycleHashes() {
        int lifetimeYears = schedulerProperties.getLifetimeYears();
        LocalDate cutoffDate = LocalDate.now().minusYears(lifetimeYears);

        log.info("Запуск очистки URL старше {} лет (дата отсечки: {})", lifetimeYears, cutoffDate);

        List<Hash> recycledHashes = urlRepository.deleteOldUrlsAndReturnHashes(cutoffDate);
        log.info("Удалено {} старых URL-ов. Освобождено хэшей: {}", recycledHashes.size(), recycledHashes);

        if (!recycledHashes.isEmpty()) {
            hashRepository.saveAll(recycledHashes);
            log.info("Хэши успешно возвращены в пул.");
        }
    }
}