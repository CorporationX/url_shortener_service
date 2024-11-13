package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Testcontainers
@EnableAsync
public class HashCacheTest {
//TODO asdfasd
    private static final long CAPACITY = 5L;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("testdb")
            .withUsername("admin")
            .withPassword("admin")
            .withInitScript("schema_for_hashgenerator.sql");

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private ExecutorService executorService;

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
        assertEquals("sfdg45", resultHash);
//        Mockito.verify(executorService,Mockito.never()).execute(Mockito.any());
    }

    @Test
    public void testHashCacheGetHashCache_shouldRunExecutorService() {
        // TODO
    }

//    @Test
//    public void testGenerateBatch_insertsHashesIntoDatabase() throws InterruptedException {
//        hashGenerator.generateBatch();
//        Thread.sleep(2000);
//
//        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hash", Long.class);
//        List<Hash> hashes = hashRepository.findAll();
//
//        assertAll(
//                () -> assertEquals(HASH_RANGE, count),
//                () -> assertEquals("5CfaC5", hashes.get(5).getHash()),
//                () -> assertEquals("5Cc5Ca", hashes.get(2).getHash()),
//                () -> assertEquals("5CjaC5", hashes.get(9).getHash())
//        );
//
//    }
}