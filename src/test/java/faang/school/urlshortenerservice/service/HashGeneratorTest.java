package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Test
    public void generateBatch_SaveHashes() {
        List<Long> numbers = Arrays.asList(1L, 2L);
        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(numbers);
        when(base62Encoder.encode(eq(numbers))).thenReturn(Arrays.asList("a", "b"));

        hashGenerator.generateBatch();

        verify(hashRepository).saveAll(anyList());
    }

    @Test
    public void getHashes_WhenEnoughHashesAvailable_ReturnHashes() {
        List<String> expectedHashes = Arrays.asList("hash1", "hash2");
        when(hashRepository.getHashBatch(eq(2))).thenReturn(expectedHashes);

        List<String> hashes = hashGenerator.getHashes(2);

        assertEquals(expectedHashes, hashes);
        verify(hashRepository, never()).saveAll(any(List.class));
    }

    @Test
    public void getHashAsync_WhenEnoughHashesAvailable_ReturnHashes() {
        List<String> expectedHashes = Arrays.asList("hash1", "hash2");
        when(hashRepository.getHashBatch(eq(2))).thenReturn(expectedHashes);

        CompletableFuture<List<String>> futureHashes = hashGenerator.getHashAsync(2);

        assertNotNull(futureHashes);
        futureHashes.thenAccept(hashes -> assertEquals(expectedHashes, hashes));
        verify(hashRepository, never()).saveAll(any(List.class));
    }
}
