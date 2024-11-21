package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.util.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HashRepository hashRepository;

    @Test
    public void testGetUniqueNumbers() {
        Long sequenceValueBefore = getSeqValue();
        int count = 5;
        List<Long> sequence = hashRepository.getUniqueNumbers(count);
        assertEquals(count, sequence.size());
        assertEquals(sequenceValueBefore + 5, getSeqValue());
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

    private Long getSeqValue() {
        return jdbcTemplate.queryForObject("select last_value from  unique_number_seq", Long.class);
    }
}
