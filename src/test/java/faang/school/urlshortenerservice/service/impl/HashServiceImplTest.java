package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.encoder.Encoder;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SpringBootTest
@Testcontainers
@Transactional
class HashServiceImplTest {

    @Autowired
    private HashRepository hashRepository;
    @Autowired
    private Encoder encoder;
    @Autowired
    private Executor asyncTaskExecutor;
    @Autowired
    private ShortenerProperties shortenerProperties;
    @Autowired
    private HashService hashService;

    private Hash hash1;
    private Hash hash2;


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
    @BeforeEach
    void setUp() {
        hash1 = Hash.builder().hash("1111").build();
        hash2 = Hash.builder().hash("2222").build();
    }

    @Test
    @DisplayName("Test of generation batch of hashes")
    void testGenerateAndSaveHashes() {
        int quantity = shortenerProperties.batchSize() * shortenerProperties.multiplier();
        hashService.generateAndSaveHashes();
        List<Hash> hashes =  hashRepository.findAll();
        Assertions.assertEquals(quantity, hashes.size());
    }

    @Test
    @DisplayName("Test of generation of hashes")
    void testGenerateHashes() {
        int quantity = 5;
        List<Hash> hashes = hashService.generateHashes(quantity);
        Assertions.assertEquals(quantity, hashes.size());
    }

    @Test
    @DisplayName("Test of async generation of hashes")
    void testGenerateHashesAsync() {
        int quantity = 5;
        CompletableFuture<List<Hash>> hashes = hashService.generateHashesAsync(quantity);
        Assertions.assertEquals(quantity, hashes.join().size());
    }

    @Test
    @DisplayName("Test saving hashes")
    void testSaveHashes() {
        List<Hash> hashes = new ArrayList<>();
        hashes.add(hash1);
        hashes.add(hash2);
        hashService.saveHashes(hashes);

        List<Hash> readedHashes =  hashRepository.findAll();

        Assertions.assertEquals(hash1, readedHashes.get(0));
        Assertions.assertEquals(hash2, readedHashes.get(1));
    }

    @Test
    @DisplayName("Test reading new hashes")
    void testReadFreeHashes() {
        int queueSize = shortenerProperties.queueSize();
        List<Hash> hashes = hashService.readFreeHashes(queueSize);

        Assertions.assertEquals(queueSize, hashes.size());

    }

    @Test
    @DisplayName("Test async reading new hashes")
    void testReadFreeHashesAsync() {
        int queueSize = shortenerProperties.queueSize();
        CompletableFuture<List<Hash>> hashes = hashService.readFreeHashesAsync(queueSize);

        Assertions.assertEquals(queueSize, hashes.join().size());
    }
}