package faang.school.urlshortenerservice.cleaner;

import faang.school.urlshortenerservice.config.propertis.redis.RedisProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.CustomHashRepositoryImpl;
import faang.school.urlshortenerservice.repository.UrlRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerHash {

    private final UrlRepositoryImpl urlRepositoryImpl;
    private final RedisProperties redisProperties;
    private final CustomHashRepositoryImpl hashRepository;

    @Scheduled(cron = "${hash.cleaner.cron}")
    @Async("hashGeneratorExecutor")
    public void cleanerAsync() {
        cleaner();
    }

    @Transactional
    public void cleaner() {
        log.info("Cleaner start - Thread name {}", Thread.currentThread().getName());

        LocalDateTime ttl = LocalDateTime.now().minusHours(redisProperties.getUrlTtl());
        List<Hash> hashes = urlRepositoryImpl.deleteExpiredUrlsAndReturnHashes(ttl);
        if (!hashes.isEmpty()) {
            log.info("Cleaner start save batch - Thread name {}", Thread.currentThread().getName());
            hashRepository.saveAllBatched(hashes);

            log.info("Cleaner finish - Thread name {}", Thread.currentThread().getName());
        }
    }
}