package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

    @Test
    void testGenerateBatch() {
        List<Long> mockRange = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("a", "b", "c");
        List<Hash> expectedHashes = Arrays.asList(new Hash("a"), new Hash("b"), new Hash("c"));

        when(hashRepository.getUniqueNumbers(3)).thenReturn(mockRange);
        when(base62Encoder.encoder(mockRange)).thenReturn(encodedHashes);

        List<Hash> actualHashes = hashGenerator.generateBatch(3);

        assertEquals(expectedHashes.size(), actualHashes.size());
        verify(hashRepository, times(1)).getUniqueNumbers(3);
        verify(base62Encoder, times(1)).encoder(mockRange);
    }

    @Test
    void testGenerateAndSaveBatch() {
        List<Long> mockRange = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("a", "b", "c");

        when(hashRepository.getUniqueNumbers(3)).thenReturn(mockRange);
        when(base62Encoder.encoder(mockRange)).thenReturn(encodedHashes);

        hashGenerator.generateAndSaveBatch(3);

        ArgumentCaptor<List<Hash>> hashCaptor = ArgumentCaptor.forClass(List.class);
        verify(hashRepository, times(1)).saveAll(hashCaptor.capture());

        List<Hash> capturedHashes = hashCaptor.getValue();
        assertEquals(3, capturedHashes.size());
        assertEquals("a", capturedHashes.get(0).getHash());
        assertEquals("b", capturedHashes.get(1).getHash());
        assertEquals("c", capturedHashes.get(2).getHash());
    }


    @Test
    void testGetHashBatch() {
        List<String> existingHashes = new ArrayList<>(Arrays.asList("a", "b"));
        List<Long> mockRange = Collections.singletonList(3L);
        List<String> encodedHashes = Collections.singletonList("c");

        when(hashRepository.getHashBatch(3)).thenReturn(existingHashes);
        when(hashRepository.getUniqueNumbers(1)).thenReturn(mockRange);
        when(base62Encoder.encoder(mockRange)).thenReturn(encodedHashes);

        List<String> actualHashes = hashGenerator.getHashBatch(3);

        assertEquals(3, actualHashes.size());
        assertEquals(Arrays.asList("a", "b", "c"), actualHashes);
        verify(hashRepository, times(1)).getHashBatch(3);
    }

    @Test
    void testGetHashBatchAsync() throws Exception {
        List<String> expectedHashes = Arrays.asList("a", "b", "c");

        ReflectionTestUtils.setField(hashGenerator, "hashRepository", hashRepository);

        when(hashRepository.getHashBatch(any(Long.class))).thenReturn(expectedHashes);

        CompletableFuture<List<String>> actualFuture = hashGenerator.getHashBatchAsync(3);

        assertEquals(expectedHashes, actualFuture.get());
        verify(hashRepository, times(1)).getHashBatch(3);
    }
}
