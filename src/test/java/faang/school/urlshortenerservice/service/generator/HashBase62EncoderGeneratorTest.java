package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashBase62EncoderGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashBase62EncoderGenerator hashGenerator;

    private static final int BATCH_SIZE = 100;

    @Test
    void generateBatch_ShouldReturnHashes_WhenSuccessful() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", BATCH_SIZE);

        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> encodedHashes = List.of("a", "b", "c");
        List<String> savedHashes = List.of("a", "b", "c");

        when(hashRepository.getUniqueNumbers(BATCH_SIZE)).thenReturn(uniqueNumbers);
        when(base62Encoder.encodeBatch(uniqueNumbers)).thenReturn(encodedHashes);
        when(hashRepository.saveAllBatch(encodedHashes.toArray(new String[0]))).thenReturn(savedHashes);

        CompletableFuture<List<String>> resultFuture = hashGenerator.generateBatch();
        List<String> result = resultFuture.join();

        assertEquals(savedHashes, result);
        verify(hashRepository).getUniqueNumbers(BATCH_SIZE);
        verify(base62Encoder).encodeBatch(uniqueNumbers);
        verify(hashRepository).saveAllBatch(encodedHashes.toArray(new String[0]));
    }

    @Test
    void generateBatch_ShouldReturnFailedFuture_WhenExceptionOccurs() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", BATCH_SIZE);

        RuntimeException exception = new RuntimeException("DB error");
        when(hashRepository.getUniqueNumbers(BATCH_SIZE)).thenThrow(exception);

        CompletableFuture<List<String>> resultFuture = hashGenerator.generateBatch();

        assertTrue(resultFuture.isCompletedExceptionally());
        verify(hashRepository).getUniqueNumbers(BATCH_SIZE);
        verify(base62Encoder, never()).encodeBatch(any());
        verify(hashRepository, never()).saveAllBatch(any());
    }

    @Test
    void generateBatch_ShouldLogError_WhenGenerationFails() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", BATCH_SIZE);

        RuntimeException exception = new RuntimeException("DB error");
        when(hashRepository.getUniqueNumbers(BATCH_SIZE)).thenThrow(exception);

        hashGenerator.generateBatch();

        verify(hashRepository).getUniqueNumbers(BATCH_SIZE);
    }
}