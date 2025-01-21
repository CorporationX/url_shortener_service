package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.BaseContextIT;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanerSchedulerIT extends BaseContextIT {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private CleanerScheduler cleanerScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredDate = now.minusYears(2);
        LocalDateTime validDate = now.minusYears(1);

        insertTestData("hash1", "https://valid.example.com", validDate);
        insertTestData("hash2", "https://valid2.example.com", validDate);
        insertTestData("hash3", "https://expired.example.com", expiredDate);
        insertTestData("hash4", "https://expired2.example.com", expiredDate);
    }

    @Test
    void cleanOldUrlsTest() {
        long initialCount = urlRepository.count();
        cleanerScheduler.cleanOldUrls();

        long remainingCount = urlRepository.count();

        assertEquals(4, initialCount);
        assertEquals(2, remainingCount);
    }

    private void insertTestData(String hash, String url, LocalDateTime createdAt) {
        jdbcTemplate.update(
                "INSERT INTO url (hash, url, created_at) VALUES (?, ?, ?)",
                hash, url, createdAt
        );
    }
}
