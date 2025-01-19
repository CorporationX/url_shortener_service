package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashWarmupService implements CommandLineRunner {
    private final HashCacheService hashCacheService;

    @Override
    public void run(String... args) {
            hashCacheService.getHashesFromDatabaseAndWaitUntilDone();
            hashCacheService.addHashToLocalCacheIfNecessary();
            log.info("Cache warmup complete");
    }
}
