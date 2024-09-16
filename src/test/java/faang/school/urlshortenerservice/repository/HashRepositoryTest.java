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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HashRepositoryTest {

    private static final int BATCH_SIZE = 3;

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

    private HashRepository hashRepository;

    @BeforeEach
    void setup() {
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