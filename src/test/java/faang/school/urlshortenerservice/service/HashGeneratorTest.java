package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.base62Encoder.Base62Encoder;
import faang.school.urlshortenerservice.service.hashGenerator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    private static final int BATCH_SIZE_UNIQUE_NUMBERS = 5;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @Mock
    private HashConfig hashConfig;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void testGenerateAndSaveHashBatch() {
        when(hashConfig.getBatchSizeUniqueNumbers()).thenReturn(BATCH_SIZE_UNIQUE_NUMBERS);
        when(hashRepository.getUniqueNumbers(BATCH_SIZE_UNIQUE_NUMBERS)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
        when(base62Encoder.generateHashList(anyList())).thenReturn(List.of("hash1"));

        hashGenerator.generateAndSaveHashBatch();

        verify(hashRepository).getUniqueNumbers(BATCH_SIZE_UNIQUE_NUMBERS);
        verify(base62Encoder).generateHashList(anyList());
        verify(hashRepository).saveAllBatched(anyList());
    }

    @Test
    void testGetHashes_WhenHashesAreLessThanBatchSize_ShouldGenerateAndSaveMore() {
        when(hashRepository.getHashBatch(BATCH_SIZE_UNIQUE_NUMBERS)).thenReturn(new ArrayList<>(List.of("hash1", "hash2")));
        when(hashConfig.getBatchSizeUniqueNumbers()).thenReturn(BATCH_SIZE_UNIQUE_NUMBERS);
        when(hashRepository.getUniqueNumbers(BATCH_SIZE_UNIQUE_NUMBERS)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
        when(base62Encoder.generateHashList(anyList())).thenReturn(List.of("hash1"));

        List<String> hashes = hashGenerator.getHashes(BATCH_SIZE_UNIQUE_NUMBERS);

        assertEquals(2, hashes.size());
        verify(hashRepository).getHashBatch(BATCH_SIZE_UNIQUE_NUMBERS);
        verify(hashRepository).getUniqueNumbers(anyInt());
        verify(base62Encoder).generateHashList(anyList());
        verify(hashRepository).saveAllBatched(anyList());
    }

    @Test
    void testGetHashes_WhenHashesAreEqualToBatchSize_ShouldNotGenerateMore() {
        long batchSize = 5;
        when(hashRepository.getHashBatch(batchSize)).thenReturn(List.of("hash1", "hash2", "hash3", "hash4", "hash5"));

        List<String> hashes = hashGenerator.getHashes(batchSize);

        // Тестируем результат
        assertEquals(5, hashes.size());
        verify(hashRepository).getHashBatch(batchSize);
        verify(hashRepository, never()).getUniqueNumbers(anyInt());
    }

    @Test
    void testGetHashesAsync() throws Exception {
        when(hashRepository.getHashBatch(BATCH_SIZE_UNIQUE_NUMBERS)).thenReturn(new ArrayList<>(List.of("hash1", "hash2")));
        when(hashConfig.getBatchSizeUniqueNumbers()).thenReturn(BATCH_SIZE_UNIQUE_NUMBERS);
        when(hashRepository.getUniqueNumbers(BATCH_SIZE_UNIQUE_NUMBERS)).thenReturn(List.of(1L, 2L, 3L, 4L, 5L));
        when(base62Encoder.generateHashList(anyList())).thenReturn(List.of("hash1"));

        CompletableFuture<List<String>> futureHashes = hashGenerator.getHashesAsync(BATCH_SIZE_UNIQUE_NUMBERS);
        List<String> hashes = futureHashes.get();

        assertEquals(2, hashes.size());
        verify(hashRepository).getHashBatch(BATCH_SIZE_UNIQUE_NUMBERS);
        verify(hashRepository).getUniqueNumbers(anyInt());
        verify(base62Encoder).generateHashList(anyList());
        verify(hashRepository).saveAllBatched(anyList());
    }
}
