package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.util.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private HashRepository hashRepository;

    @Test
    public void testGetUniqueNumbers() {
        int count = 5;
        List<Long> sequence = hashRepository.getUniqueNumbers(count);
        assertEquals(count, sequence.size());
    }

    @Test
    public void testGetBatch() {
        Long beforeCount = hashRepository.getHashCount();
        List<String> hashes = hashRepository.getHashBatch();
        Long afterCount = hashRepository.getHashCount();
        assertEquals(beforeCount - hashes.size(), afterCount);
    }

    @Test
    public void testSaveBatch() {
        Long beforeCount = hashRepository.getHashCount();
        List<String> hashes = hashRepository.getHashBatch();
        hashRepository.saveBatch(hashes);
        assertEquals(beforeCount, hashRepository.getHashCount());
    }
}
