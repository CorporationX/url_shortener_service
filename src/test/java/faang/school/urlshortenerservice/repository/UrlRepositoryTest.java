package faang.school.urlshortenerservice.repository;

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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UrlRepositoryTest {

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

    private UrlRepository urlRepository;

    @BeforeEach
    void setup() {
        urlRepository = new UrlRepository(jdbcTemplate);

        jdbcTemplate.execute("DROP TABLE IF EXISTS url");
        jdbcTemplate.execute("CREATE TABLE url (hash VARCHAR(6) PRIMARY KEY, url TEXT NOT NULL, created_at TIMESTAMP)");
    }

    @Test
    void testSave() {
        String hash = "abc123";
        String url = "https://example.com";

        urlRepository.save(hash, url);

        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM url WHERE hash = ?", hash);

        assertEquals(1, result.size());
        Map<String, Object> row = result.get(0);
        assertEquals(hash, row.get("hash"));
        assertEquals(url, row.get("url"));
    }

    @Test
    void testFindUrlByHash() {
        String hash = "abc123";
        String expectedUrl = "https://www.example.com";

        jdbcTemplate.update("INSERT INTO url (hash, url) VALUES (?, ?)", hash, expectedUrl);

        String result = urlRepository.findUrl(hash);

        assertEquals(expectedUrl, result);
    }

    @Test
    void testFindUrlByHash_NonExistentHash() {
        String nonExistentHash = "nonexistent";

        String result = urlRepository.findUrl(nonExistentHash);

        assertNull(result);
    }

    @Test
    void testDeleteUrlsAndFreeHashes() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldDate = now.minusDays(7);
        LocalDateTime futureDate = now.plusDays(7);

        jdbcTemplate.update("INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)", "hash1", "url", oldDate);
        jdbcTemplate.update("INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)", "hash2", "url", oldDate);
        jdbcTemplate.update("INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)", "hash3", "url", futureDate);

        List<String> deletedHashes = urlRepository.deleteUrlsAndFreeHashes(now);

        assertEquals(2, deletedHashes.size());
        assertTrue(deletedHashes.contains("hash1"));
        assertTrue(deletedHashes.contains("hash2"));

        int remainingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM url", Integer.class);
        assertEquals(1, remainingCount);
    }

}