package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.BaseContextIT;
import faang.school.urlshortenerservice.scheduler.hash_cleaner.HashCleanerScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Sql("/db/test_sql/insert_url_records.sql")
public class HashCleanerSchedulerIT extends BaseContextIT {

    @Autowired
    private HashCleanerScheduler scheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void moveOldHashesToFreeHashesTest() {
        scheduler.moveOldHashesToFreeHashes();
        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertEquals(0, countByHashes("url", List.of("hash3", "hash4")));
                    assertEquals(2, countByHashes("hash", List.of("hash3", "hash4")));
                });
    }

    private int countByHashes(String tableName, List<String> hashes) {
        String placeholders = String.join(",", Collections.nCopies(hashes.size(), "?"));
        String query = String.format("SELECT count(*) FROM %s WHERE hash IN (%s)", tableName, placeholders);
        return Objects.requireNonNullElse(jdbcTemplate.queryForObject(query, Integer.class, hashes.toArray()), 0);
    }
}
