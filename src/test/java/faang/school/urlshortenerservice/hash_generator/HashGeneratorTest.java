package faang.school.urlshortenerservice.hash_generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Captor
    ArgumentCaptor<Iterable<Hash>> hashIterableCaptor;

    public final int BATCH_SIZE = 100;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", BATCH_SIZE);
    }

    @Test
    public void generateBatch_shouldGenerateAndSaveHashes() {
        // Arrange
        var uniqueNumbers = List.of(1L, 2L, 3L);
        var encodedHashes = List.of("hash1", "hash2", "hash3");
        var expectedHashEntities = encodedHashes.stream()
                .map(hash -> Hash.builder().hash(hash).build())
                .toList();

        when(hashRepository.getUniqueNumbers(BATCH_SIZE)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        // Act
        var result = hashGenerator.generateBatch();

        // Assert
        assertEquals(encodedHashes, result);
        verify(hashRepository).getUniqueNumbers(BATCH_SIZE);
        verify(base62Encoder).encode(uniqueNumbers);

        verify(hashRepository).saveAll(hashIterableCaptor.capture());
        var savedItems = hashIterableCaptor.getValue();
        var savedItemsCount = 0;
        for (var item : savedItems) {
            savedItemsCount++;
            assertTrue(expectedHashEntities.stream()
                    .anyMatch(expected -> expected.getHash().equals((item).getHash())));
        }
        assertEquals(expectedHashEntities.size(), savedItemsCount);
    }

    @Test
    public void generateBatchAsync_shouldReturnCompletableFuture() throws ExecutionException, InterruptedException {
        // Arrange
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L);
        List<String> encodedHashes = List.of("hash1", "hash2", "hash3");

        when(hashRepository.getUniqueNumbers(BATCH_SIZE)).thenReturn(uniqueNumbers);
        when(base62Encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);

        // Act
        var future = hashGenerator.generateBatchAsync();

        // Assert
        assertNotNull(future);
        var result = future.get();
        assertEquals(encodedHashes, result);
    }

    @Test
    public void generateBatch_shouldHandleEmptyResult() {
        // Arrange
        when(hashRepository.getUniqueNumbers(BATCH_SIZE)).thenReturn(List.of());
        when(base62Encoder.encode(List.of())).thenReturn(List.of());

        // Act
        var result = hashGenerator.generateBatch();

        // Assert
        assertTrue(result.isEmpty());

        verify(hashRepository).saveAll(hashIterableCaptor.capture());
        var savedItems = hashIterableCaptor.getValue();
        assertFalse(savedItems.iterator().hasNext());
    }

    @Test
    public void generateBatch_shouldUseBatchSizeFromConfig() {
        // Arrange
        int newBatchSize = 50;
        ReflectionTestUtils.setField(hashGenerator, "batchSize", newBatchSize);

        when(hashRepository.getUniqueNumbers(newBatchSize)).thenReturn(List.of());
        when(base62Encoder.encode(List.of())).thenReturn(List.of());

        // Act
        hashGenerator.generateBatch();

        // Assert
        verify(hashRepository).getUniqueNumbers(newBatchSize);
    }
}