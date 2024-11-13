package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.dto.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@EnableAsync
public class HashGeneratorTest {
    private static final long HASH_RANGE = 10L;
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("testdb")
            .withUsername("admin")
            .withPassword("admin")
            .withInitScript("schema_for_hashgenerator.sql");
    @Autowired
    private HashRepository hashRepository;


    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("hash.range", () -> HASH_RANGE);
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateBatch_insertsHashesIntoDatabase() throws InterruptedException {
        hashGenerator.generateBatch();
        Thread.sleep(2000);

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hash", Long.class);
        List<Hash> hashes = hashRepository.findAll();

        assertAll(
                () -> assertEquals(HASH_RANGE, count),
                () -> assertEquals("5CfaC5", hashes.get(5).getHash()),
                () -> assertEquals("5Cc5Ca", hashes.get(2).getHash()),
                () -> assertEquals("5CjaC5", hashes.get(9).getHash())
        );

    }
}
