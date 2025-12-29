package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "cleaner.cron=0 0 2 * * ?",
        "cleaner.retention-years=1",
        "cleaner.batch-size=1000",
        "cleaner.retry.max-attempts=3",
        "cleaner.retry.delay-ms=1000"
})
class HashCacheIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (postgres.isRunning()) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
        }
    }

    @Autowired
    private HashCacheService hashCache;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        if (!postgres.isRunning()) {
            return;
        }
        jdbcTemplate.update("TRUNCATE TABLE hash CASCADE");
        jdbcTemplate.update("ALTER SEQUENCE unique_number_seq RESTART WITH 1");
        
        for (int i = 0; i < 50; i++) {
            hashRepository.save(List.of("hash" + i));
        }
    }

    @Test
    void getHash_WhenCacheInitialized_ShouldReturnHash() {
        if (!postgres.isRunning()) {
            return;
        }
        String hash = hashCache.getHash();
        
        assertThat(hash).isNotNull();
        assertThat(hashCache.size()).isGreaterThan(0);
    }

    @Test
    void getHash_WhenCacheEmpty_ShouldUseFallback() {
        if (!postgres.isRunning()) {
            return;
        }
        jdbcTemplate.update("TRUNCATE TABLE hash CASCADE");
        
        String hash = hashCache.getHash();
        
        assertThat(hash).isNotNull();
    }

    @Test
    void getHash_ConcurrentAccess_ShouldBeThreadSafe() throws InterruptedException {
        if (!postgres.isRunning()) {
            return;
        }
        int threads = 10;
        int hashesPerThread = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        Set<String> retrievedHashes = new HashSet<>();
        
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < hashesPerThread; j++) {
                        String hash = hashCache.getHash();
                        synchronized (retrievedHashes) {
                            retrievedHashes.add(hash);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();
        
        assertThat(retrievedHashes.size()).isEqualTo(threads * hashesPerThread);
    }

    @Test
    void size_ShouldReturnCurrentCacheSize() {
        if (!postgres.isRunning()) {
            return;
        }
        int initialSize = hashCache.size();
        
        hashCache.getHash();
        
        assertThat(hashCache.size()).isLessThan(initialSize);
    }
}

