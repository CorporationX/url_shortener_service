package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private static final int BATCH_SIZE = 1000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", BATCH_SIZE);
    }

    @Test
    void testGenerateBatchSuccess() throws ExecutionException, InterruptedException {
        // Given
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<String> encodedHashes = Arrays.asList("1", "2", "3", "4", "5");

        when(hashRepository.getUniqueNumbers(BATCH_SIZE)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        // When
        CompletableFuture<Integer> result = hashGenerator.generateBatch();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo(encodedHashes.size());

        verify(hashRepository, times(1)).getUniqueNumbers(BATCH_SIZE);
        verify(base62Encoder, times(1)).encode(uniqueNumbers);
        verify(hashRepository, times(1)).save(encodedHashes);
    }

    @Test
    void testGenerateBatchCallsRepositoryWithCorrectBatchSize() throws ExecutionException, InterruptedException {
        // Given
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("1", "2", "3");

        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenReturn(encodedHashes);

        // When
        hashGenerator.generateBatch().get();

        // Then
        verify(hashRepository).getUniqueNumbers(BATCH_SIZE);
    }

    @Test
    void testGenerateBatchEncodesAllNumbers() throws ExecutionException, InterruptedException {
        // Given
        List<Long> uniqueNumbers = Arrays.asList(100L, 200L, 300L);
        List<String> encodedHashes = Arrays.asList("1C", "3E", "4W");

        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        // When
        hashGenerator.generateBatch().get();

        // Then
        verify(base62Encoder).encode(uniqueNumbers);
    }

    @Test
    void testGenerateBatchSavesAllHashes() throws ExecutionException, InterruptedException {
        // Given
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L);
        List<String> encodedHashes = Arrays.asList("hash1", "hash2");

        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenReturn(encodedHashes);

        // When
        hashGenerator.generateBatch().get();

        // Then
        verify(hashRepository).save(encodedHashes);
    }

    @Test
    void testGenerateBatchHandlesException() {
        // Given
        when(hashRepository.getUniqueNumbers(anyInt()))
                .thenThrow(new RuntimeException("Database error"));

        // When
        CompletableFuture<Integer> result = hashGenerator.generateBatch();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isCompletedExceptionally()).isTrue();

        verify(hashRepository).getUniqueNumbers(BATCH_SIZE);
        verify(base62Encoder, never()).encode(any());
        verify(hashRepository, never()).save(any());
    }

    @Test
    void testGenerateBatchHandlesEncodingException() {
        // Given
        List<Long> uniqueNumbers = Arrays.asList(1L, 2L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenThrow(new RuntimeException("Encoding error"));

        // When
        CompletableFuture<Integer> result = hashGenerator.generateBatch();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isCompletedExceptionally()).isTrue();

        verify(hashRepository).getUniqueNumbers(BATCH_SIZE);
        verify(base62Encoder).encode(uniqueNumbers);
        verify(hashRepository, never()).save(any());
    }

    @Test
    void testGenerateBatchReturnsCorrectCount() throws ExecutionException, InterruptedException {
        // Given
        int expectedCount = 50;
        List<Long> uniqueNumbers = generateLongList(expectedCount);
        List<String> encodedHashes = generateStringList(expectedCount);

        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(anyList())).thenReturn(encodedHashes);

        // When
        CompletableFuture<Integer> result = hashGenerator.generateBatch();

        // Then
        assertThat(result.get()).isEqualTo(expectedCount);
    }

    private List<Long> generateLongList(int size) {
        return Arrays.asList(new Long[size]);
    }

    private List<String> generateStringList(int size) {
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            strings[i] = "hash" + i;
        }
        return Arrays.asList(strings);
    }
}