package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGeneratorService, "alphabet", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        ReflectionTestUtils.setField(hashGeneratorService, "base", 62);
    }

    @Test
    void testGenerateHash_Success() {

        long batchSize = 5;
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);

        hashGeneratorService.generateHash(batchSize);

        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void testGenerateHash_EmptyNumbers_ThrowsException() {

        long batchSize = 5;
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class,
                () -> hashGeneratorService.generateHash(batchSize));
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
    void testGetHashes_NeedAdditionalGeneration() {

        int batchSize = 5;
        List<String> initialHashes = new ArrayList<>(List.of("a", "b"));
        List<String> additionalHashes = new ArrayList<>(List.of("c", "d", "e"));
        List<Long> uniqueNumbers = new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L));

        when(hashRepository.getAndDeletedHashBatch(batchSize))
                .thenReturn(initialHashes)
                .thenReturn(additionalHashes);
        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(uniqueNumbers);

        List<String> result = hashGeneratorService.getHashes(batchSize);

        verify(hashRepository).saveAll(anyList());
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