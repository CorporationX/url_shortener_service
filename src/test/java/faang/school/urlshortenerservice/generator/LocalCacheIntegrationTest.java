package faang.school.urlshortenerservice.generator;

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
    private Executor hashGeneratorExecutor;

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
       //localCache = new LocalCache(hashGenerator, hashGeneratorExecutor, properties, poolProperties);
    }

    @Test
    void testGetHashSuccess() throws InterruptedException {
        hashGenerator.generateHash(10);
        Thread.sleep(1000);
        String hash1 = localCache.getHash();
        String hash2 = localCache.getHash();
        String hash3 = localCache.getHash();

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotNull(hash3);
    }

    @Test
    void testGetHashAsyncFill() throws InterruptedException {
        hashGenerator.generateHash(properties.getCapacity());
        Thread.sleep(1000);

        for (int i = 0; i < (properties.getCapacity()); i++) {
            Thread.sleep(10);
            String hash = localCache.getHash();
            assertNotNull(hash);
        }

    }
}