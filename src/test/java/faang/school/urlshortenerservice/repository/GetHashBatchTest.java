package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetHashBatchTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CustomHashRepository customHashRepository;

    @Test
    public void testGetHashBatch() {
        int n = 3;
        List<String> expectedHashes = List.of("hash1", "hash2", "hash3");

        String sql = "WITH deleted AS ( " +
                "  DELETE FROM hash " +
                "  WHERE hash IN ( " +
                "    SELECT hash FROM hash ORDER BY RANDOM() LIMIT ? " +
                "  ) " +
                "  RETURNING hash " +
                ") " +
                "SELECT hash FROM deleted";

        when(jdbcTemplate.queryForList(eq(sql), eq(String.class), anyInt())).thenReturn(expectedHashes);

        List<String> actualHashes = customHashRepository.getHashBatch(n);

        assertEquals(expectedHashes, actualHashes);

        verify(jdbcTemplate, times(1)).queryForList(eq(sql), eq(String.class), eq(n));
    }
}