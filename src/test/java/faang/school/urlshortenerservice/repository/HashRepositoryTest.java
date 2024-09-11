package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.JdbcAwareTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashRepositoryTest extends JdbcAwareTest {

    private static final int BATCH_SIZE = 3;

    private HashRepository hashRepository;

    @BeforeEach
    void setup() {
        super.initJdbcTemplate();
        hashRepository = new HashRepository(jdbcTemplate, BATCH_SIZE);

        jdbcTemplate.execute("DROP SEQUENCE IF EXISTS unique_numbers_seq");
        jdbcTemplate.execute("CREATE SEQUENCE unique_numbers_seq START WITH 1 INCREMENT BY 1 NO CYCLE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS hash");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS hash (hash VARCHAR(6) PRIMARY KEY)");
    }

    @Test
    void testGetUniqueNumbers_retrievesUniqueNumbersAlways() {
        List<Long> numbers = hashRepository.getUniqueNumbers(5);
        var uniqueNumbers = new HashSet<>(numbers);

        assertEquals(5, numbers.size());
        assertEquals(5, uniqueNumbers.size());

        for (Long number : numbers) {
            assertTrue(number > 0 && number <= 5);
        }

        var moreNumbers = hashRepository.getUniqueNumbers(BATCH_SIZE);
        assertEquals(BATCH_SIZE, moreNumbers.size());
        assertTrue(uniqueNumbers.addAll(moreNumbers));
    }

    @Test
    void testSave() {
        var hashes = Arrays.asList("hash1", "hash2", "hash3");

        hashRepository.save(hashes);

        var savedHashes = jdbcTemplate.queryForList("SELECT hash FROM hash", String.class);
        assertTrue(savedHashes.containsAll(hashes));
    }

    @Test
    void testGetHashBatch() {
        var initialHashes = Arrays.asList("hash1", "hash2", "hash3", "hash4", "hash5");
        hashRepository.save(initialHashes);

        var retrievedHashes = hashRepository.getHashBatch();

        assertEquals(BATCH_SIZE, retrievedHashes.size());
        assertTrue(initialHashes.containsAll(retrievedHashes));

        var remainingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hash", Integer.class);
        assertEquals(2, remainingCount);
    }
}