package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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
    private HashService transactionService;

    private final int batchSize = 3;

    @Test
    public void testSaveHashBatch() {
        int batchSize = 3;
        List<Long> nums = Arrays.asList(1L, 2L, 3L);
        List<String> encodedNums = Arrays.asList("a", "b", "c");
        List<Hash> hashBatch = Arrays.asList(new Hash("a"), new Hash("b"), new Hash("c"));

        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(nums);
        when(base62Encoder.encodeBatch(nums)).thenReturn(encodedNums);

        transactionService.saveHashBatch(batchSize);

        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        verify(base62Encoder, times(1)).encodeBatch(nums);
        verify(hashRepository, times(1)).saveAll(hashBatch);
    }

    @Test
    void testGetHashBatchSuccess() {
        List<String> mockHashBatch = List.of("x", "y", "z");
        when(hashRepository.removeAndGetHashBatch(batchSize)).thenReturn(mockHashBatch);

        List<String> result = transactionService.removeAndGetHashes(batchSize);

        verify(hashRepository, times(1)).removeAndGetHashBatch(batchSize);
        assertEquals(mockHashBatch, result);
    }

    @Test
    void testGetHashBatchEmpty() {
        List<String> emptyHashBatch = List.of();
        when(hashRepository.removeAndGetHashBatch(batchSize)).thenReturn(emptyHashBatch);

        List<String> result = transactionService.removeAndGetHashes(batchSize);

        verify(hashRepository, times(1)).removeAndGetHashBatch(batchSize);
        assertEquals(emptyHashBatch, result);
    }
}
