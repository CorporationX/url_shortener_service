package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorServiceTest {

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashGeneratorService hashGeneratorService;

    @Test
    void testGenerateHash_WhenLockAcquired_Success_SaveHashes() {

        long batchSize = 5;
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L);
        when(hashRepository.acquireAdvisoryXactLock(anyInt())).thenReturn(true);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);

        hashGeneratorService.generateHash(batchSize);

        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void generateHash_WhenLockNotAcquired_ShouldDoNothing() {

        when(hashRepository.acquireAdvisoryXactLock(anyInt())).thenReturn(false);

        hashGeneratorService.generateHash(100);

        verify(hashRepository, never()).getUniqueNumbers(anyLong());
        verify(hashRepository, never()).saveAll(any());
    }

    @Test
    void generateHash_WhenNoUniqueNumbers_ShouldThrowException() {

        when(hashRepository.acquireAdvisoryXactLock(anyInt())).thenReturn(true);
        when(hashRepository.getUniqueNumbers(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> hashGeneratorService.generateHash(100));
    }

    @Test
    void getHashes_WhenEnoughHashesExist_ShouldReturnImmediately() {

        List<String> expected = List.of("hash1", "hash2");
        when(hashRepository.getAndDeletedHashBatch(2)).thenReturn(expected);

        List<String> result = hashGeneratorService.getHashes(2);

        assertEquals(expected, result);
        verify(hashRepository, never()).saveAll(any());
    }

    @Test
    void testGetHashes_EnoughHashesInStorage() {

        int batchSize = 5;
        List<String> expectedHashes = List.of("a", "b", "c", "d", "e");
        when(hashRepository.getAndDeletedHashBatch(batchSize)).thenReturn(expectedHashes);

        List<String> result = hashGeneratorService.getHashes(batchSize);

        assertEquals(expectedHashes, result);
        verify(hashRepository, never()).saveAll(any());
    }

    @Test
    void testGetHashesAsync_ReturnsCompletedFuture() {

        int batchSize = 5;
        List<String> expectedHashes = List.of("a", "b", "c", "d", "e");
        when(hashRepository.getAndDeletedHashBatch(batchSize)).thenReturn(expectedHashes);

        CompletableFuture<List<String>> future = hashGeneratorService.getHashesAsync(batchSize);

        assertTrue(future.isDone());
        assertEquals(expectedHashes, future.join());
    }
}