package faang.school.urlshortenerservice.repository.hash;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class HashRepositoryTest {
    private static final int HASH_BATCH_SIZE = 3;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) throws InterruptedException {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("hash.batch-size", () -> HASH_BATCH_SIZE);
        Thread.sleep(1000);
    }

    @Test
    void testGetUniqueNumbers() {
        int n = 100;
        List<Long> numbers = hashRepository.getUniqueNumbers(n);
        assertEquals(n, numbers.size());
    }

    @Test
    void testSave() {
        List<String> hashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5");
        hashRepository.save(hashes);
        List<String> hashesInDb = jdbcTemplate.queryForList("SELECT hash FROM hash", String.class);
        assertEquals(hashes.size(), hashesInDb.size());
    }

    @Test
    void testGetHashBatch() {
        jdbcTemplate.update("delete from hash");
        List<String> hashes = List.of("hash6", "hash7", "hash8", "hash9", "hash10");
        hashRepository.save(hashes);
        List<String> removedHashes = hashRepository.getHashBatch();
        assertEquals(HASH_BATCH_SIZE, removedHashes.size());
        List<String> hashesInDb = jdbcTemplate.queryForList("SELECT hash FROM hash", String.class);
        assertEquals(hashes.size() - HASH_BATCH_SIZE, hashesInDb.size());
    }

    @Test
    void testGetHashWithCustomBatch() {
        jdbcTemplate.update("delete from hash");
        List<String> hashes = List.of("hash6", "hash7", "hash8", "hash9", "hash10");
        hashRepository.save(hashes);
        List<String> removedHashes = hashRepository.getHashWithCustomBatch(hashes.size());
        assertEquals(hashes.size(), removedHashes.size());
        List<String> hashesInDb = jdbcTemplate.queryForList("SELECT hash FROM hash", String.class);
        assertEquals(0, hashesInDb.size());
    }
}