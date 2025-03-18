package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.config.ThreadPoolProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class HashGeneratorIntegrationTest {

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Base62Encoder base62Encoder;

    @Autowired
    private HashGeneratorProperties properties;

    @Autowired
    private ThreadPoolProperties threadPoolProperties;

    @BeforeEach
    void setUp() {
        hashRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testGenerateHashSuccess() {
        resetSequence();
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

    @Test
    @Order(4)
    public void testGetAndCheckUniqueHashesSuccess() {
        resetSequence();
        List<Hash> hashes = hashGenerator.getHashes(10);

        assertEquals(10, hashes.size());

        Set<String> uniqueHashes = hashes.stream()
                .map(Hash::getHash)
                .collect(Collectors.toSet());
        assertEquals(10, uniqueHashes.size());
    }

    public void resetSequence() {
        entityManager.createNativeQuery("ALTER SEQUENCE unique_numbers_seq RESTART WITH 1").executeUpdate();
    }
}