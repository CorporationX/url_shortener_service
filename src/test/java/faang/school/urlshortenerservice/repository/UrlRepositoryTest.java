package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.JdbcAwareTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlRepositoryTest extends JdbcAwareTest {

    private UrlRepository urlRepository;

    @BeforeEach
    void setup() {
        super.initJdbcTemplate();
        urlRepository = new UrlRepository(jdbcTemplate);

        jdbcTemplate.execute("DROP TABLE IF EXISTS url");
        jdbcTemplate.execute("CREATE TABLE url (hash VARCHAR(6) PRIMARY KEY, url TEXT NOT NULL, created_at TIMESTAMP)");
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