package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CleanerSchedulerTest {

    @Container
    protected static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withDatabaseName("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void setup() {
        UrlRepository urlRepository = new UrlRepository(jdbcTemplate);
        HashRepository hashRepository = new HashRepository(jdbcTemplate, 10);
        cleanerScheduler = new CleanerScheduler(urlRepository, hashRepository, "P1Y");

        jdbcTemplate.execute("DROP TABLE IF EXISTS url");
        jdbcTemplate.execute("CREATE TABLE url (hash VARCHAR(6) PRIMARY KEY, url TEXT NOT NULL, created_at TIMESTAMP)");
        jdbcTemplate.execute("DROP TABLE IF EXISTS hash");
        jdbcTemplate.execute("CREATE TABLE hash (hash VARCHAR(6) PRIMARY KEY)");
    }

    @Test
    void testCleanUpOutdatedUrls() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearAgo = now.minusYears(1);
        LocalDateTime twoYearsAgo = now.minusYears(2);

        jdbcTemplate.update("INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)", "hash1", "http://example1.com", twoYearsAgo);
        jdbcTemplate.update("INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)", "hash2", "http://example2.com", oneYearAgo.plusDays(1));
        jdbcTemplate.update("INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)", "hash3", "http://example3.com", now);

        cleanerScheduler.cleanUpOutdatedUrls();

        assertCleanupResult(2, List.of("hash2", "hash3"), List.of("hash1"));
    }

    private void assertCleanupResult(int expectedRemainingUrlCount, List<String> expectedRemainingHashes, List<String> expectedFreedHashes) {
        int remainingUrlCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM url", Integer.class);
        assertEquals(expectedRemainingUrlCount, remainingUrlCount);

        List<String> remainingHashes = jdbcTemplate.queryForList("SELECT hash FROM url", String.class);
        assertTrue(remainingHashes.containsAll(expectedRemainingHashes));

        List<String> savedHashes = jdbcTemplate.queryForList("SELECT hash FROM hash", String.class);
        assertEquals(expectedFreedHashes.size(), savedHashes.size());
        assertTrue(savedHashes.containsAll(expectedFreedHashes));
    }
}