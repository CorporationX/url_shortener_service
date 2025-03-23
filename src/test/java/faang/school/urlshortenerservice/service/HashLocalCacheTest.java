package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.model.Hash;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class HashLocalCacheTest {

    @Autowired
    private ShortenerProperties shortenerProperties;
    @Autowired
    private HashService hashService;
    @Autowired
    private HashLocalCache hashLocalCache;

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:13")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @DisplayName("Test get free hashes from queue")
    void testGetFreeHashFromQueue() {
        Hash hash1 = hashLocalCache.getFreeHashFromQueue();
        Hash hash2 = hashLocalCache.getFreeHashFromQueue();
        Assertions.assertNotEquals(hash1.getHash(), hash2.getHash());
    }
}