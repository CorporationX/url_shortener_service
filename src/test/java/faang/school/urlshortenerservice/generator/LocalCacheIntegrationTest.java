package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
public class LocalCacheIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private LocalCache localCache;

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private Base62Encoder base62Encoder;

    @Autowired
    private LocalCacheProperties properties;

    @BeforeEach
    void setUp() {
        hashRepository.deleteAll();
    }

    @Test
    void testGetHashSuccess() {
        String hash1 = localCache.getHash();
        String hash2 = localCache.getHash();
        String hash3 = localCache.getHash();

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotNull(hash3);
    }

    @Test
    void testGetHashAsyncFill() {
        properties.setFillPercentage(50);

        for (int i = 0; i < properties.getCapacity() * 2; i++) {
            String hash = localCache.getHash();
            assertNotNull(hash);
        }
    }
}