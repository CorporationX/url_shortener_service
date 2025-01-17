package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerScheduler {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-size.save}")
    private int saveBatchSize;
    @Value("${hash.scheduler.cleaner.retentionPeriodYears}")
    private int retentionPeriodYears;

    @Async("cleanerExecutor")
    @Transactional
    @Scheduled(cron = "${hash.scheduler.cleaner.cron}")
    public void deleteOldRecords() {
        log.info("Start deleting old urls.");
        LocalDateTime createdAt = LocalDateTime.now().minusYears(retentionPeriodYears);
        List<String> oldHashes =
                urlRepository.getOldUrls(createdAt).stream()
                        .map(Url::getHash)
                        .toList();

        hashRepository.save(oldHashes, saveBatchSize, jdbcTemplate);
        log.info("Successfully deleting old urls and adding their hashes to the database.");
    }
}
