package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.cache.HashInitializer;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.ContainerCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class HashGeneratorIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = ContainerCreator.POSTGRES_CONTAINER;

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
    private HashRepository hashRepository;

    @MockBean
    private HashInitializer hashInitializer;

    @BeforeEach
    public void init() {
        hashRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("Should generate specified number of hashes and save them in database")
    void testGenerateHashesBatch() {
        int customAmount = 15;

        hashGenerator.generateHashesBatch(customAmount);

        hashGenerator.shutdownExecutor();

        long count = hashRepository.count();
        assertEquals(customAmount, count);
    }
}
