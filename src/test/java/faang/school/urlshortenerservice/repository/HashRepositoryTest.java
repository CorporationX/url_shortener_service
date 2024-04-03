package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = "spring.test.database.replace=none")
class HashRepositoryTest {

    @Autowired
    private HashRepository hashRepository;

    @Container
    public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withInitScript("init1.sql");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRE_SQL_CONTAINER.start();
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetUniqueNumbers() {
        //Arrange
        int count = 2;
        //Act
        List<Long> result = hashRepository.getUniqueNumbers(count);
        //Assert
        assertEquals(result.size(), count);
    }

    @Test
    void testGetAndDeleteHashBatch() {
        //Arrange
        String hash1 = "hash1";
        Hash hash = new Hash(hash1);
        hashRepository.save(hash);
        int count = 1;

        //Act
        List<String> result = hashRepository.getAndDeleteHashBatch(count);

        //Assert
        assertEquals(result.size(),count);
    }
}