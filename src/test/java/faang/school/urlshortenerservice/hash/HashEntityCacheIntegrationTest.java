package faang.school.urlshortenerservice.hash;


import faang.school.urlshortenerservice.ServiceTemplateApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ServiceTemplateApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class HashEntityCacheIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HashProperties hashProperties;

    @Autowired
    private HashCache hashCache;

    @Autowired
    private ThreadPoolTaskExecutor threadPool;

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
        String sql = "SELECT COUNT(*) FROM free_hash_set";
        Long resultCount = jdbcTemplate.queryForObject(sql, Long.class);

        Long expectedCount = (long) hashProperties.getGenerateSize() - hashProperties.getCacheCapacity() ;
        assertEquals(expectedCount, resultCount);
    }

    @Test
    public void testGenerateHash_ExactlyOnce() throws InterruptedException {
        int count = (int) (hashProperties.getCacheCapacity() * (1.0 - hashProperties.getLowThresholdFactor()));
        for (int i = 0; i < count; i++) {
            hashCache.getHash();
        }

        Runnable hashGetter = () -> hashCache.getHash();
        threadPool.execute(hashGetter);
        threadPool.execute(hashGetter);

        SECONDS.sleep(1);

        Long expectedCount = 2L * hashProperties.getGenerateSize() - hashProperties.getCacheCapacity() - hashProperties.getHashBatchSize();

        String sql = "SELECT COUNT(*) FROM free_hash_set";
        Long resultCount = jdbcTemplate.queryForObject(sql, Long.class);
        assertEquals(expectedCount, resultCount);
    }
}
