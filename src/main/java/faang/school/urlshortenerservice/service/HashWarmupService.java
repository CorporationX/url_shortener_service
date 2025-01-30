package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlShortenerProperties;
import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashWarmupService implements CommandLineRunner {
    private final HashService hashService;
    private final UrlShortenerProperties urlShortenerProperties;
    private final ArrayBlockingQueue<Hash> localCache;

    @Override
    public void run(String... args) {
        List<Hash> hashesToLocalCache = hashService.generateBatch(urlShortenerProperties.hashAmountToLocalCache());
        localCache.addAll(hashesToLocalCache);
        hashService.uploadHashInDatabaseIfNecessary();
        log.info("Cache warmup complete");
    }
}
