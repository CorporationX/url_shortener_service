package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    @DisplayName("Generate batch synchronously and save to repository")
    public void testGenerateBatch() {
        int numberOfBatch = 5;
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> encodedHashes = List.of("1", "2", "3", "4", "5");

        when(hashRepository.getUniqueNumbers(numberOfBatch)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        List<String> result = hashGenerator.generateBatch(numberOfBatch);

        assertEquals(encodedHashes, result);
        verify(hashRepository).save(encodedHashes);
        verify(hashRepository).getUniqueNumbers(numberOfBatch);
        verify(base62Encoder).encode(uniqueNumbers);
    }

    @Test
    @DisplayName("Generate batch asynchronously")
    public void testGenerateBatchAsync() {
        int numberOfBatch = 5;
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        List<String> encodedHashes = List.of("1", "2", "3", "4", "5");

        when(hashRepository.getUniqueNumbers(numberOfBatch)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        CompletableFuture<List<String>> futureResult = hashGenerator.generateBatchAsync(numberOfBatch);

        List<String> result = futureResult.join();  // Получаем результат асинхронно
        assertEquals(encodedHashes, result);
        verify(hashRepository).save(encodedHashes);
        verify(hashRepository).getUniqueNumbers(numberOfBatch);
        verify(base62Encoder).encode(uniqueNumbers);
    }
}