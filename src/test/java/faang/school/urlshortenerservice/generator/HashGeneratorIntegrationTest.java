package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
public class HashGeneratorIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Base62Encoder base62Encoder;

    @Autowired
    private HashGeneratorProperties properties;

    @BeforeEach
    void setUp() {
        hashRepository.deleteAll();
    }

    @Test
    void testGenerateHash_Success() {
        properties.setBatchSize(3);

        hashGenerator.generateHash();

        List<Hash> hashes = hashRepository.findAll();
        assertEquals(3, hashes.size());

        List<String> expectedHashes = List.of(
                base62Encoder.encodeSingle(1L),
                base62Encoder.encodeSingle(2L),
                base62Encoder.encodeSingle(3L)
        );

        List<String> actualHashes = hashes.stream()
                .map(Hash::getHash)
                .toList();
        assertEquals(expectedHashes, actualHashes);
    }
}