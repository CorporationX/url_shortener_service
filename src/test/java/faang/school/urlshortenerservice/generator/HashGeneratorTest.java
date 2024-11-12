package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class HashGeneratorTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("admin")
            .withPassword("admin")
            .withInitScript("schema_for_hashgenerator.sql")
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));


    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @MockBean
    @Autowired
    private HashRepository hashRepository;

//    @MockBean
    @Autowired
    private Base62Encoder base62Encoder;

    @Value("${hash.range:1000}")
    private int range;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateBatch_insertsHashesIntoDatabase() throws InterruptedException {
        // Arrange
        List<String> hashes = List.of("45tfrf", "jfu445", "094kd3");
        int range = 3; // Example range
//        when(hashRepository.getNextRange(range)).thenReturn(List.of(1L,2L,3L));
//        when(base62Encoder.encode(List.of(1L,2L,3L))).thenReturn(hashes);


        Thread.sleep(5000);
        // Act
        hashGenerator.generateBatch();

        // Assert - проверяем, что значения были вставлены в таблицу
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hash", Long.class);
//        List<Hash>hashesResult = hashRepository.findAll();
        assertEquals(hashes.size(), count);
//        assertEquals(hashes.size(), hashesResult.size());
    }
}
