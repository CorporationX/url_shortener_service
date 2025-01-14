package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HashRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String urlWithRewrite = postgres.getJdbcUrl() + "?reWriteBatchedInserts=true";
        registry.add("spring.datasource.url", () -> urlWithRewrite);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER SEQUENCE unique_number_seq RESTART WITH 1");
    }

    @Test
    @DisplayName("Get list of unique numbers from sequence in DB: return first 5")
    void test_getUniqueNumbers_Success() {
        List<Long> result = hashRepository.getUniqueNumbers(5);
        System.out.println(result);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(1, result.get(0));
    }

    @Test
    @DisplayName("Get empty list if queries amount < 1")
    void test_getUniqueNumbers_Fail_EmptyList() {
        List<Long> result = hashRepository.getUniqueNumbers(0);
        System.out.println(result);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Get list of unique numbers from sequence in DB: return first 5, then next 3")
    void test_getUniqueNumbers_Success_DoubleRequest() {
        List<Long> resultOne = hashRepository.getUniqueNumbers(5);
        System.out.println(resultOne);

        assertNotNull(resultOne);
        assertEquals(5, resultOne.size());
        assertEquals(1, resultOne.get(0));

        List<Long> resultTwo = hashRepository.getUniqueNumbers(3);
        System.out.println(resultTwo);

        assertNotNull(resultTwo);
        assertEquals(3, resultTwo.size());
        assertEquals(6, resultTwo.get(0));
    }

    @Test
    @DisplayName("Get hashes in batch")
    void test_getHashBatch_Success() {
        int entriesAmount = 30;
        populateDb(entriesAmount);

        List<Hash> result = hashRepository.getHashBatch(10);
        result.forEach(x -> System.out.println(x.getShortUrl()));
        Long remainingHashes = hashRepository.count();

        assertNotNull(result);
        assertEquals(10, result.size());
        assertEquals(20, remainingHashes);
    }

    private void populateDb(int amount) {
        for (int i = 0; i < amount; i++) {
            Hash hash = new Hash(String.valueOf(i));
            hashRepository.save(hash);
        }
    }
}