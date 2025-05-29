package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.ContainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ContainersConfiguration.class)
@DisplayName("HashService Test")
class HashServiceTest {

    @Autowired
    private HashService hashService;

    @Test
    @Transactional
    @DisplayName("Get hashes")
    void getHashes() {
        int count = 3;
        List<String> hashes = hashService.getHashes(count);
        assertEquals(count, hashes.size());
        assertTrue(hashes.stream().distinct().count() == count, "Hashes should be unique");
    }

    @Test
    @Transactional
    @DisplayName("Save free hashes")
    void saveFreeHashes() {
        List<String> hashes = List.of("abc123", "def456");
        hashService.saveFreeHashes(hashes);

        List<String> retrievedHashes = hashService.getHashes(2);
        assertEquals(2, retrievedHashes.size());
    }
}