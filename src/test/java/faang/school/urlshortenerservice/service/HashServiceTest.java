package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @InjectMocks
    private HashService hashService;
    @Mock
    private Base62Encoder base62Encoder;
    @Mock
    private JdbcTemplate  jdbcTemplate;

    @Test
    void testSaveHashBatch() {
        List<Long> uniqueHashList = List.of(1L,2L,3L);

        hashService.saveHashesBatch(uniqueHashList);

        verify(base62Encoder, times(1)).encodeNumbers(uniqueHashList);
        verify(jdbcTemplate, times(1)).batchUpdate(anyString(), (BatchPreparedStatementSetter) any());
    }
}
