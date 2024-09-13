package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerationService;
import faang.school.urlshortenerservice.util.BaseEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@Testcontainers
public class HashGenerationTest {
    @Container
    public static final PostgreSQLContainer postgresContainer =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withInitScript("init.sql");
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        postgresContainer.start();
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private  HashRepository hashRepository;
    @Autowired
    private BaseEncoder baseEncoder;
    @Autowired
    private HashGenerationService hashGenerationService;

    @BeforeEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void init() {
        assertTrue(postgresContainer.isRunning());

        hashRepository.deleteAllHashes();
        hashRepository.resetSequence();
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 1000L})
    void getHashes(long amount) {
        List<String> hashes = hashGenerationService.getHashes(amount);
        assertEquals(amount, hashes.size());
    }
}
