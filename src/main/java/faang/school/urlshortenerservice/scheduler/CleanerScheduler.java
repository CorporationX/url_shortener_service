package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${cleaner.cron.cleanTime:0 0 0 * * ?}")
    private String cleanTime;

    @Scheduled(cron = "${cleaner.cron.expression}")
    @Transactional
    public void cleanOldUrls() {
        log.info("Запуск очистки устаревших URL...");
        LocalDateTime oneYearAgo = LocalDateTime.now(ZoneOffset.UTC).minusYears(1);
        List<String> deletedHashes = urlRepository.deleteOlderThan(oneYearAgo);
        log.info("Удалено {} записей из urls", deletedHashes.size());

        List<Hash> hashesToSave = deletedHashes.stream()
                .map(hashValue -> new Hash(hashValue))
                .toList();
//todo:saveAll
        hashRepository.saveAll(hashesToSave);

        log.info("Добавлено {} записей в таблицу hash", hashesToSave.size());
    }
}
