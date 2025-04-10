package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashWarmupService implements CommandLineRunner {
    private final HashCache hashCache;

    @Override
    public void run(String... args) {
        hashCache.warmupCache();
        log.info("Cache warmup complete");
    }
}
