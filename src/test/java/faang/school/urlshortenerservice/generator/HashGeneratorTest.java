package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private TransactionService transactionService;

    private final int batchSize = 5;

    @Test
    public void testGetHashesWhenSizeIsEqualToBatchSize() {
        List<String> hashesFromRemoveAndGet = List.of("hash1", "hash2", "hash3", "hash4", "hash5");
        when(transactionService.removeAndGetHashes(batchSize)).thenReturn(hashesFromRemoveAndGet);

        List<String> result = hashGenerator.getHashes(batchSize);

        assertEquals(batchSize, result.size());
        assertTrue(result.containsAll(hashesFromRemoveAndGet));

        verify(transactionService).removeAndGetHashes(batchSize);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void testGetHashesWhenSizeIsLessThanBatchSize() {
        List<String> hashesFromRemoveAndGet = List.of("hash1", "hash2");
        List<String> hashesFromSaveHashBatch = List.of("hash3", "hash4", "hash5");

        int remaining = batchSize - hashesFromRemoveAndGet.size();
        when(transactionService.removeAndGetHashes(batchSize)).thenReturn(hashesFromRemoveAndGet);
        when(transactionService.removeAndGetHashes(remaining)).thenReturn(hashesFromSaveHashBatch);

        List<String> result = hashGenerator.getHashes(batchSize);

        assertEquals(batchSize, result.size());
        assertTrue(result.containsAll(hashesFromRemoveAndGet));
        assertTrue(result.containsAll(hashesFromSaveHashBatch));

        verify(transactionService).removeAndGetHashes(batchSize);
        verify(transactionService).saveHashBatch(remaining);
        verify(transactionService).removeAndGetHashes(remaining);
        verifyNoMoreInteractions(transactionService);
    }
}

