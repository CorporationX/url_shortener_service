package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@Transactional
public class LocalCacheIntegrationTest {

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
        localCache.fillCacheSync(properties.getCapacity());
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
    void testGetHashAsyncFill() throws InterruptedException {
        properties.setFillPercentage(50);

        for (int i = 0; i < (properties.getCapacity() * 20); i++) {
            Thread.sleep(10);
            String hash = localCache.getHash();
            assertNotNull(hash);
        }

    }
}