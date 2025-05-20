package faang.school.urlshortenerservice.andreev.cache;

import faang.school.urlshortenerservice.andreev.encoder.Base62Encoder;
import faang.school.urlshortenerservice.andreev.exception.HashGenerationException;
import faang.school.urlshortenerservice.andreev.repository.HashRepository;
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


import static faang.school.urlshortenerservice.andreev.exception.ErrorMessage.HASH_GENERATION_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {
    @InjectMocks
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    private final int batchSize = 5;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "batchSize", batchSize);
    }

    @Test
    void testGenerateBatch_success() {
        List<Long> numbers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<String> hashes = Arrays.asList("a", "b", "c", "d", "e");

        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(numbers);
        when(base62Encoder.encode(numbers)).thenReturn(hashes);

        hashGenerator.generateBatch();

        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        verify(base62Encoder, times(1)).encode(numbers);
        verify(hashRepository, times(1)).save(hashes);
    }

    @Test
    void testGenerateBatch_failure_shouldThrowException() {
        when(hashRepository.getUniqueNumbers(batchSize)).thenThrow(new RuntimeException("DB error"));

        HashGenerationException exception = assertThrows(HashGenerationException.class, () ->
            hashGenerator.generateBatch());

        assertEquals(HASH_GENERATION_FAILED, exception.getMessage());
        verify(hashRepository, times(1)).getUniqueNumbers(batchSize);
        verify(hashRepository, never()).save(anyList());
    }

    @Test
    void testGetHashes_enoughHashesInRepository() {
        List<String> hashes = Arrays.asList("x1", "x2", "x3");

        when(hashRepository.getHashBatch(3)).thenReturn(hashes);

        List<String> result = hashGenerator.getHashes(3);

        assertEquals(hashes, result);
        verify(hashRepository, times(1)).getHashBatch(3);
        verify(hashRepository, never()).save(anyList());
    }

    @Test
    void testGetHashesAsync_shouldReturnCompletableFuture() throws Exception {
        List<String> hashes = Arrays.asList("h1", "h2");

        when(hashRepository.getHashBatch(2)).thenReturn(hashes);

        CompletableFuture<List<String>> future = hashGenerator.getHashesAsync(2);
        List<String> result = future.get();

        assertEquals(hashes, result);
        verify(hashRepository, times(1)).getHashBatch(2);
    }
}