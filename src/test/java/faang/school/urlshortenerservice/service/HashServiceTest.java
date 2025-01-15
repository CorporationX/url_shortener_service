package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashService hashService;

    @Test
    void saveHashesShouldSaveAllHashes() {
        List<HashEntity> hashEntities = List.of(
                HashEntity.builder().hash("hash1").isUsed(false).build(),
                HashEntity.builder().hash("hash2").isUsed(false).build(),
                HashEntity.builder().hash("hash3").isUsed(false).build()
        );

        hashService.saveHashes(hashEntities);

        ArgumentCaptor<List<HashEntity>> captor = ArgumentCaptor.forClass(List.class);

        verify(hashRepository, times(1)).saveAll(captor.capture());
        assertEquals(hashEntities, captor.getValue());
    }

    @Test
    void getUniqueNumbersShouldReturnListOfUniqueNumbers() {
        int n = 5;
        List<Long> uniqueNumbers = List.of(1L, 2L, 3L, 4L, 5L);
        when(hashRepository.getUniqueNumbers(n)).thenReturn(uniqueNumbers);

        List<Long> result = hashService.getUniqueNumbers(n);

        assertEquals(uniqueNumbers, result);
        verify(hashRepository, times(1)).getUniqueNumbers(n);
    }

    @Test
    void getHashBatchShouldReturnAndDeleteBatchOfHashes() {
        int batchSize = 3;
        List<String> hashBatch = List.of("hash1", "hash2", "hash3");
        when(hashRepository.getHashBatch(batchSize)).thenReturn(hashBatch);

        List<String> result = hashService.getHashBatch(batchSize);

        assertEquals(hashBatch, result);
        verify(hashRepository, times(1)).getHashBatch(batchSize);
    }

    @Test
    void saveHashesShouldHandleEmptyList() {
        List<HashEntity> emptyList = Collections.emptyList();

        hashService.saveHashes(emptyList);

        verify(hashRepository, times(1)).saveAll(emptyList);
    }

    @Test
    void getUniqueNumbersShouldThrowExceptionWhenRepositoryFails() {
        int n = 5;
        when(hashRepository.getUniqueNumbers(n)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> hashService.getUniqueNumbers(n));
        assertEquals("Database error", exception.getMessage());
        verify(hashRepository, times(1)).getUniqueNumbers(n);
    }

    @Test
    void getHashBatchShouldReturnEmptyListIfNoHashesAvailable() {
        int batchSize = 3;
        when(hashRepository.getHashBatch(batchSize)).thenReturn(Collections.emptyList());

        List<String> result = hashService.getHashBatch(batchSize);

        assertTrue(result.isEmpty());
        verify(hashRepository, times(1)).getHashBatch(batchSize);
    }

    @Test
    void getHashBatchShouldThrowExceptionWhenRepositoryFails() {
        int batchSize = 3;
        when(hashRepository.getHashBatch(batchSize)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> hashService.getHashBatch(batchSize));
        assertEquals("Database error", exception.getMessage());
        verify(hashRepository, times(1)).getHashBatch(batchSize);
    }

    @Test
    public void testSaveUnusedHashes() {
        List<String> hashes = Arrays.asList(
                "hash1", "hash2", "hash3", "hash4", "hash5",
                "hash6", "hash7", "hash8", "hash9", "hash10"
        );

        hashService.saveUnusedHashes(hashes);

        verify(hashRepository, times(1)).saveUnusedHashes(anyList());

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository, times(1)).saveUnusedHashes(captor.capture());
    }
}