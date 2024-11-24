package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.base.AbstractBaseContext;
import faang.school.urlshortenerservice.repository.jpa.HashRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashServiceTest extends AbstractBaseContext {

    @Autowired
    private HashRepositoryImpl hashRepository;

    @Autowired
    private HashServiceImpl hashService;

    @BeforeEach
    public void init() {
        jdbcTemplate.update("DELETE FROM hash");
        jdbcTemplate.update("ALTER SEQUENCE hash_unique_number_seq RESTART WITH 1");
    }

    @Test
    public void testGetUniqueNumbers() {
        List<Long> numbers = hashService.getUniqueNumbers(10);

        assertEquals(10, numbers.size());
        assertEquals(1L, numbers.get(0));
        assertEquals(2L, numbers.get(1));
        assertEquals(3L, numbers.get(2));
        assertEquals(4L, numbers.get(3));
        assertEquals(5L, numbers.get(4));
    }

    @Test
    public void testSaveBatch() {
        List<String> hashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5", "hash6");

        hashRepository.saveBatch(hashes);

        List<String> savedHashes = jdbcTemplate.queryForList("SELECT * from hash", String.class);

        assertEquals(hashes.size(), savedHashes.size());
    }

    @Test
    public void testGetHashes() {
        List<String> hashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5");
        hashRepository.saveBatch(hashes);

        List<String> removedHashes = hashService.getHashes(3).join();

        assertEquals(3, removedHashes.size());
    }
}
