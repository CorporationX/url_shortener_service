package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.ContainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ContainersConfiguration.class)
@DisplayName("HashRepository Test")
class HashRepositoryTest {

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    @Transactional
    void setUp() {
        jdbcTemplate.update("DELETE FROM hash", Collections.emptyMap());
    }

    @Test
    @Transactional
    @DisplayName("Get unique numbers")
    void getUniqueNumbers() {
        int count = 5;
        List<Long> numbers = hashRepository.getUniqueNumbers(count);
        assertEquals(count, numbers.size());
        assertTrue(numbers.stream().distinct().count() == count, "Numbers should be unique");
    }

    @Test
    @Transactional
    @DisplayName("Save and get hashes")
    void saveAndGetHashes() {
        List<String> hashes = List.of("abc123", "def456");
        hashRepository.saveHashes(hashes);

        List<String> retrievedHashes = hashRepository.getHashes(2);
        assertEquals(2, retrievedHashes.size());
        assertTrue(hashes.containsAll(retrievedHashes));

        List<String> remainingHashes = hashRepository.getHashes(2);
        assertTrue(remainingHashes.isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("Get count of hashes")
    void getCountOfHashes() {
        List<String> hashes = List.of("abc123", "def456");
        hashRepository.saveHashes(hashes);

        long count = hashRepository.getCountOfHashes();
        assertEquals(2, count);

        hashRepository.getHashes(2);
        count = hashRepository.getCountOfHashes();
        assertEquals(0, count);
    }
}