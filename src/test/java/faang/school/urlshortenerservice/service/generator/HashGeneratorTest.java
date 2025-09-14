package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.common.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private Base62Encoder base62Encoder;
    @InjectMocks
    private HashGeneratorImpl generator;

    @Test
    void testGenerateBatch_Success() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> expectedHashes = List.of("hash1", "hash2", "hash3");

        when(hashRepository.getHashNumbers(anyInt()))
                .thenReturn(numbers);
        when(base62Encoder.encode(numbers))
                .thenReturn(expectedHashes);

        List<String> result = generator.generateBatch();

        assertEquals(expectedHashes, result);
        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void testGenerateBatch_EmptyResult() {
        List<Long> emptyList = List.of();

        when(hashRepository.getHashNumbers(anyInt()))
                .thenReturn(emptyList);

        List<String> result = generator.generateBatch();

        assertTrue(result.isEmpty());
        verify(hashRepository, never()).saveAll(anyList());
    }

    @Test
    void testGenerateBatchAsync_Success() {
        List<Long> numbers = List.of(1L, 2L, 3L);
        List<String> expectedHashes = List.of("hash1", "hash2", "hash3");

        when(hashRepository.getHashNumbers(anyInt()))
                .thenReturn(numbers);
        when(base62Encoder.encode(numbers))
                .thenReturn(expectedHashes);

        CompletableFuture<List<String>> future = generator.generateBatchAsync();

        try {
            List<String> result = future.get();

            assertEquals(expectedHashes, result);
            verify(hashRepository).saveAll(anyList());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}

