package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.cache.LocalCache;
import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.config.ThreadPoolProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.concurrent.Executor;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@Transactional
public class LocalCacheIntegrationTest {

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private Executor taskExecutor;

    @Autowired
    private LocalCacheProperties properties;

    @Autowired
    private ThreadPoolProperties poolProperties;

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LocalCache localCache;

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
    void testGetHashWithAsyncFillSuccess() {
        for (int i = 0; i < (properties.getCapacity() * 20); i++) {
            String hash = localCache.getHash();
            assertNotNull(hash);
        }
    }
}