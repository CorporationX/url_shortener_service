package faang.school.urlshortenerservice.hash_generator;

import faang.school.urlshortenerservice.model.Hash;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

    private static final String BATCH_SIZE_FIELD_NAME = "batchSize";
    private static final int BATCH_SIZE = 100;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashGenerator, BATCH_SIZE_FIELD_NAME, BATCH_SIZE);
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
        ReflectionTestUtils.setField(hashGenerator, BATCH_SIZE_FIELD_NAME, newBatchSize);

        when(hashRepository.getUniqueNumbers(newBatchSize)).thenReturn(List.of());
        when(base62Encoder.encode(List.of())).thenReturn(List.of());

        // Act
        hashGenerator.generateBatch();

        // Assert
        verify(hashRepository).getUniqueNumbers(newBatchSize);
    }

    @Test
    void getHashes_whenEnoughHashesInRepository_returnAllHashesAtOnce() {
        // Arrange
        long amount = 5;
        var testHashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5");
        var hashesInRepo = testHashes.stream().map(Hash::new).toList();
        when(hashRepository.getHashBatch(amount)).thenReturn(hashesInRepo);

        // Act
        var result = hashGenerator.getHashes(amount);

        // Assert
        assertEquals(5, result.size());
        assertEquals(testHashes, result);
        verify(hashRepository, times(1)).getHashBatch(anyLong());
        verifyNoMoreInteractions(hashRepository);
    }

    @Test
    void getHashes_whenNotEnoughHashesInRepository_generateOneBatch() {
        // Arrange
        long amount = 5;

        when(hashRepository.getHashBatch(amount))
                .thenReturn(new ArrayList<>(List.of(new Hash("hash1"), new Hash("hash2"), new Hash("hash3"))));
        when(hashRepository.getHashBatch(2))
                .thenReturn(new ArrayList<>(List.of(new Hash("hash4"), new Hash("hash5"))));

        // Act
        var result = hashGenerator.getHashes(amount);

        // Assert
        assertEquals(5, result.size());
        assertEquals(List.of("hash1", "hash2", "hash3", "hash4", "hash5"), result);

        verify(hashRepository, times(1)).getHashBatch(5);
        verify(hashRepository, times(1)).getHashBatch(2);
    }

    @Test
    void getHashes_whenNeedMultipleBatches_generateMultipleTimes() {
        // Arrange
        long amount = 10;

        when(hashRepository.getHashBatch(amount))
                .thenReturn(new ArrayList<>(List.of(new Hash("hash1"), new Hash("hash2"))));
        when(hashRepository.getHashBatch(8))
                .thenReturn(new ArrayList<>(List.of(new Hash("hash3"), new Hash("hash4"), new Hash("hash5"))));
        when(hashRepository.getHashBatch(5))
                .thenReturn(new ArrayList<>(List.of(new Hash("hash6"), new Hash("hash7"))));
        when(hashRepository.getHashBatch(3))
                .thenReturn(new ArrayList<>(List.of(new Hash("hash8"), new Hash("hash9"), new Hash("hash10"))));

        // Act
        var result = hashGenerator.getHashes(amount);

        // Assert
        assertEquals(10, result.size());
        verify(hashRepository, times(4)).getHashBatch(anyLong());
    }

    @Test
    void getHashes_whenAmountIsZero_returnEmptyList() {
        // Arrange
        long amount = 0;
        when(hashRepository.getHashBatch(amount)).thenReturn(Collections.emptyList());

        // Act
        var result = hashGenerator.getHashes(amount);

        // Assert
        assertTrue(result.isEmpty());
        verify(hashRepository, times(1)).getHashBatch(0);
        verifyNoMoreInteractions(hashRepository);
    }
}