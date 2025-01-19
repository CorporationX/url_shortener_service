package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private TransactionService transactionService;

    private final int batchSize = 5;

    @Test
    void testGetHashBatchWithSufficientHashes() throws Exception {
        List<String> mockHashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5");

        when(transactionService.getHashBatch(batchSize)).thenReturn(mockHashes);

        CompletableFuture<List<String>> result = hashGenerator.getHashBatch(batchSize);

        assertEquals(mockHashes, result.get());

        verify(transactionService, never()).saveHashBatch(anyInt());
    }

    @Test
    void testGetHashBatchWithInsufficientHashes() throws Exception {
        List<String> mockHashes = new ArrayList<>(List.of("hash1", "hash2"));
        List<String> mockAdditionalHashes = new ArrayList<>(List.of("hash3", "hash4", "hash5"));

        when(transactionService.getHashBatch(batchSize)).thenReturn(mockHashes);
        when(transactionService.getHashBatch(3)).thenReturn(mockAdditionalHashes);

        CompletableFuture<List<String>> result = hashGenerator.getHashBatch(batchSize);

        assertEquals(List.of("hash1", "hash2", "hash3", "hash4", "hash5"), result.get());

        verify(transactionService).getHashBatch(batchSize);
        verify(transactionService).getHashBatch(3);
    }

    @Test
    void testGetHashBatchWithNoHashes() throws Exception {
        List<String> mockAdditionalHashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5");

        when(transactionService.getHashBatch(batchSize)).thenReturn(List.of());
        when(transactionService.getHashBatch(batchSize)).thenReturn(mockAdditionalHashes);

        CompletableFuture<List<String>> result = hashGenerator.getHashBatch(batchSize);

        assertEquals(mockAdditionalHashes, result.get());

        verify(transactionService, times(1)).getHashBatch(batchSize);
    }
}

