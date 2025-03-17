package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
    void testGenerateHashSuccess() {
        properties.setBatchSize(3);

        hashGenerator.generateHash();

        List<Hash> hashes = hashRepository.findAll();
        //List<Hash> hashes = hashRepository.findAndDelete(3);
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

    @Test
    @Order(2)
    void testGetAndDeleteHashSuccess() {
        properties.setBatchSize(100);

        hashGenerator.generateHash();

        List<Hash> hashes = hashRepository.findAndDelete(100);
        assertEquals(100, hashes.size());

        hashes = hashRepository.findAndDelete(100);
        assertEquals(0, hashes.size());
    }

    @Test
    @Order(3)
    void testGetHashesSuccess() {
        properties.setBatchSize(100);

        hashGenerator.generateHash();

        List<Hash> hashes = hashGenerator.getHashes(100);
        assertEquals(100, hashes.size());

        hashes = hashGenerator.getHashes(100);
        assertEquals(100, hashes.size());
    }
}