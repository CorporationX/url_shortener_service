package faang.school.urlshortenerservice.service.generator.async;

import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncHashGeneratorImplTest {

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private AsyncHashGeneratorImpl asyncHashGenerator;

    @Test
    void generateBatchAsync_callsGenerateBatch() {
        asyncHashGenerator.generateBatchAsync();
        verify(hashGenerator, times(1)).generateBatch();
    }

    @Test
    void getBatchAsync_returnsExpectedBatch() throws ExecutionException, InterruptedException {
        when(hashGenerator.getBatch()).thenReturn(List.of("hash1", "hash2"));
        CompletableFuture<List<String>> future = asyncHashGenerator.getBatchAsync();
        List<String> result = future.get();
        assertEquals(List.of("hash1", "hash2"), result);
    }

    @Test
    void getBatchAsync_handlesEmptyBatch() throws ExecutionException, InterruptedException {
        when(hashGenerator.getBatch()).thenReturn(List.of());
        CompletableFuture<List<String>> future = asyncHashGenerator.getBatchAsync();
        List<String> result = future.get();
        assertTrue(result.isEmpty());
    }
}