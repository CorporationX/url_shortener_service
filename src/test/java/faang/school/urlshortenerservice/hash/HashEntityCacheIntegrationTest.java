package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.UrlShortenerApplication;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = UrlShortenerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class HashEntityCacheIntegrationTest {

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private HashCacheFiller hashCacheFiller;

    @Autowired
    private HashCache hashCache;

    @Value("${hash.generate-size}")
    private long generateSize;

    @Value("${hash.hash-batch-size}")
    private long hashBatchSize;

    @Value("${hash.low-threshold-cache-size}")
    private long lowThresholdCacheSize;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpassword");

    @BeforeAll
    public static void init() {
        System.setProperty("spring.datasource.url", POSTGRESQL_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRESQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", POSTGRESQL_CONTAINER.getPassword());
    }

    @Test
    public void testFreeHashSetState() {
        long resultCount = hashRepository.count();

        Long expectedCount = generateSize - hashBatchSize;
        assertEquals(expectedCount, resultCount);
    }


    @Test
    public void testFillHash_ExactlyOnce() throws InterruptedException {
        for (int i = 0; i < 700; i++) {
            hashCache.getHash();
        }

        hashCacheFiller.fillCacheIfNecessary();
        hashCacheFiller.fillCacheIfNecessary();
        Thread.sleep(10000);

        assertEquals(900, hashCache.getSize());
    }
}