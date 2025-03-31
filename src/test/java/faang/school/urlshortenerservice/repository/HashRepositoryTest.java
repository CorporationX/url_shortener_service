package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Slf4j
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HashRepositoryTest {

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.6")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void init() {
        entityManager.getEntityManager().createNativeQuery(
                "CREATE SEQUENCE IF NOT EXISTS unique_number_seq START WITH 1"
        ).executeUpdate();

        hashRepository.save(new Hash("123456"));
    }

    @Test
    @DisplayName("Получение уникальных чисел - возвращает запрошенное количество")
    void getUniqueNumbers_ReturnsRequestedAmount() {
        List<Long> numbers = hashRepository.getUniqueNumbers(2);
        assertEquals(List.of(1L, 2L), numbers);
    }

    @Test
    @DisplayName("Получение пакета хэшей - возвращает и удаляет хэши")
    void getHashBatch_ReturnsAndDeletesHashes() {
        hashRepository.save(new Hash("abcdef"));
        hashRepository.save(new Hash("ghijkl"));

        List<String> hashes = hashRepository.getHashBatch(1);
        assertEquals(1, hashes.size());
        assertEquals(2, hashRepository.count());
    }
}
