package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void testGenerateBatch() {
        List<Long> uniqueNumbers = List.of(1L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(anyInt());
        verify(base62Encoder).encode(1L);
        verify(hashRepository).save(anyList(), anyInt(), eq(jdbcTemplate));
    }

    @Test
    public void testGetHashBatch() {
        hashGenerator.getHashBatch(1);
        verify(hashRepository).getHashBatch(1);
    }

    @Test
    public void testTryLock_SuccessfulLock_ShouldReturnTrue() {
        when(hashRepository.tryLock()).thenReturn(1);

        boolean result = hashGenerator.tryLock();
        assertTrue(result);
    }

    @Test
    public void testTryLock_UnsuccessfulLock_ShouldReturnFalse() {
        when(hashRepository.tryLock()).thenReturn(0);

        boolean result = hashGenerator.tryLock();
        assertFalse(result);
    }

    @Test
    public void testIsHashCountBelowThreshold() {
        hashGenerator.isHashCountBelowThreshold(1);
        verify(hashRepository).isHashCountBelowThreshold(1);
    }

    @Test
    public void testUnlock() {
        hashGenerator.unlock();
        verify(hashRepository).unlock();
    }
}