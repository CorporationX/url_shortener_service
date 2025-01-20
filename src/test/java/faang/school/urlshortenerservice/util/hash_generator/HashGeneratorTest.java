package faang.school.urlshortenerservice.util.hash_generator;

import faang.school.urlshortenerservice.properties.short_url.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashProperties hashProperties;

    @Mock
    private AsyncHashGenerator asyncHashGenerator;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    void getHashesNeedToGenerateMoreTest() {
        long count = 2L;
        int dbCreateMaxCount = 2;
        int dbCreateBatchSize = 1;
        List<String> initialHashes = new ArrayList<>(List.of("hash1"));
        when(hashProperties.getDbCreateMaxCount()).thenReturn(dbCreateMaxCount);
        when(hashProperties.getDbCreateBatchSize()).thenReturn(dbCreateBatchSize);
        when(hashRepository.getHashBatch(count)).thenReturn(initialHashes);
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(List.of(2L, 3L));
        when(hashRepository.getHashBatch(count - initialHashes.size())).thenReturn(new ArrayList<>(List.of("hash2")));
        when(asyncHashGenerator.generateAndSaveHashBatch(any())).thenReturn(CompletableFuture.completedFuture(null));

        List<String> hashes = hashGenerator.getHashes(count);

        assertEquals(count, hashes.size());
        verify(hashRepository, times(2)).getHashBatch(anyLong());
        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(asyncHashGenerator, times(dbCreateMaxCount)).generateAndSaveHashBatch(any());
    }

    @Test
    void getHashesNoMoreGenerationTest() {
        long count = 2L;
        List<String> initialHashes = new ArrayList<>(List.of("hash1", "hash2"));
        when(hashRepository.getHashBatch(count)).thenReturn(initialHashes);

        List<String> hashes = hashGenerator.getHashes(count);
        assertEquals(initialHashes, hashes);
    }

    @Test
    void generateHashesTest() {
        int dbCreateMaxCount = 2;
        int dbCreateBatchSize = 1;
        when(hashProperties.getDbCreateMaxCount()).thenReturn(dbCreateMaxCount);
        when(hashProperties.getDbCreateBatchSize()).thenReturn(dbCreateBatchSize);
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(List.of(2L, 3L));
        when(asyncHashGenerator.generateAndSaveHashBatch(any())).thenReturn(CompletableFuture.completedFuture(null));

        hashGenerator.generateHashes();

        verify(hashRepository, times(1)).getUniqueNumbers(anyLong());
        verify(asyncHashGenerator, times(dbCreateMaxCount)).generateAndSaveHashBatch(any());
    }
}