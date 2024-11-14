package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.util.PostgreSQLTestContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
@Testcontainers
@EnableAsync
public class HashCacheTest {
    private static final long CAPACITY = 3L;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static PostgreSQLContainer<?> postgresContainer;

    @BeforeAll
    public static void setup() {
        postgresContainer = PostgreSQLTestContainer.getPostgresContainer();
    }

    @BeforeEach
    public void setUp() throws Exception {
        jdbcTemplate.execute("DROP SCHEMA public CASCADE;");
        jdbcTemplate.execute("CREATE SCHEMA public;");

        ClassPathResource resource = new ClassPathResource("schema_for_hashgenerator.sql");
        ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), resource);
    }

    @SpyBean
    private HashGenerator hashGenerator;

    @Autowired
    private HashCache hashCache;

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("hash_cache.capacity", () -> CAPACITY);
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Test
    public void testHashCacheGetHashCache_shouldReturnHash() {
        String resultHash = hashCache.getHash();

        assertNotNull(resultHash);
    }

    @Test
    public void testHashCacheGetHashCache_shouldRunCacheAddAllWithoutHashGenerator() throws InterruptedException {
        String resultHash = null;
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            resultHash = hashCache.getHash();
        }

        Queue<String> cache = (Queue<String>) ReflectionTestUtils.getField(hashCache, "cache");
        assertEquals(2,cache.size());
        assertEquals("hash3", resultHash);
        verify(hashGenerator, times(0)).generateBatch();
    }

}