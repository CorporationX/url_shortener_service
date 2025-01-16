package faang.school.urlshortenerservice.scheduler.hash_cleaner;

import faang.school.urlshortenerservice.UrlShortenerApplicationTests;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@Sql(value = "/db/cleaner_scheduler_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CleanerSchedulerIT extends UrlShortenerApplicationTests {

    @Autowired
    private UrlRepositoryImpl urlRepository;

    @Autowired
    private CleanerScheduler cleanerScheduler;

    @Test
    public void cleanUnusedHashesTest() {

        cleanerScheduler.cleanUnusedHashes();

        Awaitility.await()
                .atMost(20, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertNotNull(urlRepository.findOriginalUrlByHash("hash2").orElse(null));
                    assertNotNull(urlRepository.findOriginalUrlByHash("hash4").orElse(null));
                    assertNull(urlRepository.findOriginalUrlByHash("hash1").orElse(null));
                    assertNull(urlRepository.findOriginalUrlByHash("hash3").orElse(null));
                    assertTrue(doesHashExist("hash1"));
                    assertTrue(doesHashExist("hash3"));
                });
    }

    private boolean doesHashExist(String hash) {
        String sql = "SELECT EXISTS (SELECT 1 FROM hash WHERE hash = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, hash));
    }
}
