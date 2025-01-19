package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private TransactionService transactionService;

    private final int batchSize = 3;
    @Test
    void testSaveHashBatchSuccess() {
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> encodedNumbers = List.of("a", "b", "c");
        List<Hash> hashBatch = List.of(new Hash("a"), new Hash("b"), new Hash("c"));
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(uniqueNumbers);
        when(base62Encoder.encodeBatch(uniqueNumbers)).thenReturn(encodedNumbers);

        List<String> result = transactionService.saveHashBatch(batchSize);

        verify(hashRepository).getUniqueNumbers(batchSize);
        verify(base62Encoder).encodeBatch(uniqueNumbers);
        verify(hashRepository, times(1)).saveAll(hashBatch);
        assertEquals(encodedNumbers, result);
    }

    @Test
    void testGetHashBatchSuccess() {
        List<String> mockHashBatch = List.of("x", "y", "z");
        when(hashRepository.removeAndGetHashBatch(batchSize)).thenReturn(mockHashBatch);

        List<String> result = transactionService.getHashBatch(batchSize);

        verify(hashRepository, times(1)).removeAndGetHashBatch(batchSize);
        assertEquals(mockHashBatch, result);
    }

    @Test
    void testGetHashBatchEmpty() {
        List<String> emptyHashBatch = List.of();
        when(hashRepository.removeAndGetHashBatch(batchSize)).thenReturn(emptyHashBatch);

        List<String> result = transactionService.getHashBatch(batchSize);

        verify(hashRepository, times(1)).removeAndGetHashBatch(batchSize);
        assertEquals(emptyHashBatch, result);
    }
}
