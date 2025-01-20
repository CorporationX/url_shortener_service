package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlShortenerProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
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
    private final HashCacheService hashCacheService;

    @Override
    public void run(String... args) {
            hashCacheService.getHashesFromDatabaseAndWaitUntilDone();
            hashCacheService.addHashToLocalCacheIfNecessary();
            log.info("Cache warmup complete");
    }
}
