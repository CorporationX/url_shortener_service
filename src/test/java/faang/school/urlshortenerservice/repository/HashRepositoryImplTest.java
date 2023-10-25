package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashRepositoryImplTest {
    @InjectMocks
    private HashRepositoryImpl hashRepositoryImpl;
    @Mock
    private JdbcTemplate jdbcTemplate;
    private int batchSize;

    @BeforeEach
    void setUp() {
        batchSize = 5;
        ReflectionTestUtils.setField(hashRepositoryImpl, "batchSize", batchSize);
    }

    @Test
    void testGetUniqueNumbers() {
        when(jdbcTemplate.queryForList(anyString(), eq(Long.class), eq(batchSize)))
                .thenReturn(Arrays.asList(1L, 2L, 3L, 4L, 5L));

        List<Long> uniqueNumbers = hashRepositoryImpl.getUniqueNumbers();

        assertEquals(5, uniqueNumbers.size());
        assertEquals(1L, uniqueNumbers.get(0));
        assertEquals(5L, uniqueNumbers.get(4));

        verify(jdbcTemplate).queryForList(anyString(), eq(Long.class), eq(batchSize));
    }

    @Test
    void testSave() {
        List<String> hashes = Arrays.asList("hash1", "hash2", "hash3");
        batchSize = 2;
        ReflectionTestUtils.setField(hashRepositoryImpl, "batchSize", batchSize);
        hashRepositoryImpl.save(hashes);

        verify(jdbcTemplate, times(2))
                .batchUpdate(eq("INSERT INTO hash (hash) VALUES (?)"), any(BatchPreparedStatementSetter.class));
    }

    @Test
    void testGetHashBatch() {
        int n = 3;
        List<String> expectedHashes = Arrays.asList("hash1", "hash2", "hash3");

        when(jdbcTemplate.queryForList(anyString(), eq(String.class), eq(n)))
                .thenReturn(expectedHashes);

        List<String> result = hashRepositoryImpl.getHashBatch(n);

        assertEquals(expectedHashes, result);

        verify(jdbcTemplate).queryForList(anyString(), eq(String.class), eq(n));
    }
}