package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class LocalCacheIntegrationTest extends IntegrationTestBase {

    @Autowired
    LocalCache localCache;

    @Autowired
    HashRepository hashRepository;

    @Autowired
    HashGenerator hashGenerator;

    @Test
    void cacheIsFilledAfterInit() {
        Set<String> hashes = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            hashes.add(localCache.getHash());
        }

        assertEquals(5, hashes.size());
    }

    @Test
    void cacheRefillsAndSystemKeepsServingRequests() {
        Set<String> hashes = new HashSet<>();

        // taking more than fetchBatchSize
        for (int i = 0; i < 50; i++) {
            hashes.add(localCache.getHash());
        }

        assertEquals(50, hashes.size()); // all hashes are unique
    }

    @Test
    void systemEventuallyFailsIfDatabaseIsEmpty() {
        while (true) {
            try {
                localCache.getHash();
            } catch (IllegalStateException e) {
                break; // database is temporarily empty and exception was thrown
            }
        }
    }

    @Test
    void hashesAreUniqueUnderConcurrentAccess() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        Set<String> hashes = ConcurrentHashMap.newKeySet();

        List<Callable<Void>> tasks = IntStream.range(0, 100)
                .mapToObj(i -> (Callable<Void>) () -> {
                    hashes.add(localCache.getHash());
                    return null;
                })
                .toList();

        executor.invokeAll(tasks);

        assertEquals(100, hashes.size()); // thread safety
    }
}
