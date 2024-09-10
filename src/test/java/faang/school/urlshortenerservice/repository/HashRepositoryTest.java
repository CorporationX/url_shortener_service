package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HashRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withDatabaseName("testuser")
            .withPassword("testpass");

    private JdbcTemplate jdbcTemplate;
    private HashRepository hashRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setup() {
        DataSource dataSource = createDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
        hashRepository = new HashRepository(jdbcTemplate, 3);

        jdbcTemplate.execute("DROP SEQUENCE IF EXISTS unique_numbers_seq");
        jdbcTemplate.execute("CREATE SEQUENCE unique_numbers_seq START WITH 1 INCREMENT BY 1 NO CYCLE");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS hash (hash VARCHAR(6) PRIMARY KEY)");
    }

    private DataSource createDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(postgresContainer.getJdbcUrl());
        dataSource.setUsername(postgresContainer.getUsername());
        dataSource.setPassword(postgresContainer.getPassword());
        return dataSource;
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

        var moreNumbers = hashRepository.getUniqueNumbers(3);
        assertEquals(3, moreNumbers.size());
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

        assertEquals(3, retrievedHashes.size());
        assertTrue(initialHashes.containsAll(retrievedHashes));

        var remainingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hash", Integer.class);
        assertEquals(2, remainingCount);
    }
}