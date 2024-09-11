package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.JdbcAwareTest;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashGeneratorTest extends JdbcAwareTest {

    private static final int BATCH_SIZE = 5;
    private static final int HASH_LENGTH = 6;

    private HashGenerator hashGenerator;

    @BeforeEach
    void setup() {
        super.initJdbcTemplate();
        HashRepository hashRepository = new HashRepository(jdbcTemplate, BATCH_SIZE);
        Base62Encoder base62Encoder = new Base62Encoder(HASH_LENGTH);
        hashGenerator = new HashGenerator(hashRepository, base62Encoder, BATCH_SIZE);

        jdbcTemplate.execute("DROP SEQUENCE IF EXISTS unique_numbers_seq");
        jdbcTemplate.execute("CREATE SEQUENCE unique_numbers_seq START WITH 1 INCREMENT BY 1 NO CYCLE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS hash");
        jdbcTemplate.execute("CREATE TABLE hash (hash VARCHAR(6) PRIMARY KEY)");
    }

    @Test
    @Transactional
    void testGenerateBatch() {
        hashGenerator.generateBatch();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> savedHashes = jdbcTemplate.queryForList("SELECT hash FROM hash", String.class);
            assertEquals(BATCH_SIZE, savedHashes.size());
            assertTrue(savedHashes.stream().allMatch(hash -> hash.length() == HASH_LENGTH));
        });
    }

    @Test
    @Transactional
    void testMultipleBatchGeneration() {
        int iterations = 3;
        for (int i = 0; i < iterations; i++) {
            hashGenerator.generateBatch();
        }

        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> savedHashes = jdbcTemplate.queryForList("SELECT hash FROM hash", String.class);
            assertEquals(BATCH_SIZE * iterations, savedHashes.size());
            assertTrue(savedHashes.stream().allMatch(hash -> hash.length() == HASH_LENGTH));
            assertEquals(savedHashes.size(), new HashSet<>(savedHashes).size());
        });
    }

    @Test
    @Transactional
    void testHashFormat() {
        hashGenerator.generateBatch();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> savedHashes = jdbcTemplate.queryForList("SELECT hash FROM hash", String.class);
            assertTrue(savedHashes.stream().allMatch(hash -> hash.matches("[A-Za-z0-9]{6}")));
        });
    }
}