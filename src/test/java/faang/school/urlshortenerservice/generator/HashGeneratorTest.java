package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", 1000);
        ReflectionTestUtils.setField(hashGenerator, "minRange", 100);
    }

    @Test
    void testValidateConfigWithValidParameters() {
        assertDoesNotThrow(() -> hashGenerator.validateConfig());
    }

    @Test
    void testValidateConfigWithNegativeMaxRange() {
        ReflectionTestUtils.setField(hashGenerator, "maxRange", -1);
        assertThrows(IllegalStateException.class, () -> hashGenerator.validateConfig());
    }

    @Test
    void testValidateConfigWithMinRangeGreaterThanMaxRange() {
        ReflectionTestUtils.setField(hashGenerator, "minRange", 2000);
        assertThrows(IllegalStateException.class, () -> hashGenerator.validateConfig());
    }

    @Test
    void testGetHashesWithValidAmount() {
        long amount = 5;
        List<Hash> mockHashes = Arrays.asList(
            new Hash("hash1"),
            new Hash("hash2"),
            new Hash("hash3"),
            new Hash("hash4"),
            new Hash("hash5")
        );
        when(hashRepository.findAndDelete(amount)).thenReturn(mockHashes);
        when(hashRepository.count()).thenReturn(1000L);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(amount, result.size());
        verify(hashRepository).findAndDelete(amount);
    }

    @Test
    void testGetHashesWithZeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> hashGenerator.getHashes(0));
    }

    @Test
    void testGetHashesWithNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> hashGenerator.getHashes(-1));
    }

    @Test
    void testGetHashesWithAmountExceedingMaxRange() {
        assertThrows(IllegalArgumentException.class, () -> hashGenerator.getHashes(1001));
    }

    @Test
    void testGetHashesAsyncWithValidAmount() {
        long amount = 5;
        List<Hash> mockHashes = Arrays.asList(
            new Hash("hash1"),
            new Hash("hash2"),
            new Hash("hash3"),
            new Hash("hash4"),
            new Hash("hash5")
        );
        when(hashRepository.findAndDelete(amount)).thenReturn(mockHashes);
        when(hashRepository.count()).thenReturn(1000L);

        CompletableFuture<List<String>> future = hashGenerator.getHashesAsync(amount);

        assertNotNull(future);
        assertTrue(future.isDone());
        assertEquals(amount, future.join().size());
    }

    @Test
    void testGetHashesAsyncWithRepositoryError() {
        when(hashRepository.findAndDelete(anyLong())).thenThrow(new RuntimeException("Database error"));

        assertThrows(HashGenerationException.class, () -> hashGenerator.getHashesAsync(5));
    }

    @Test
    void testGenerateHashesAsyncWithAvailableLock() {
        List<Long> range = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("hash1", "hash2", "hash3");
        when(hashRepository.getNextRange(anyInt())).thenReturn(range);
        when(base62Encoder.encode(range)).thenReturn(encodedHashes);

        hashGenerator.generateHashesAsync();

        verify(hashRepository).saveAll(any());
    }

    @Test
    void testGenerateHashesAsyncWithEmptyRange() {
        when(hashRepository.getNextRange(anyInt())).thenReturn(Collections.emptyList());

        hashGenerator.generateHashesAsync();

        verify(hashRepository, never()).saveAll(any());
    }

    @Test
    void testGetHashesWithInsufficientHashes() {
        long amount = 5;
        List<Hash> initialHashes = Arrays.asList(new Hash("hash1"), new Hash("hash2"));
        when(hashRepository.findAndDelete(amount)).thenReturn(initialHashes);
        when(hashRepository.count()).thenReturn(50L);

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(2, result.size());
        verify(hashRepository, times(2)).findAndDelete(anyLong());
    }

    @Test
    void testGetHashesWithRepositoryError() {
        when(hashRepository.findAndDelete(anyLong())).thenThrow(new RuntimeException("Database error"));

        assertThrows(HashGenerationException.class, () -> hashGenerator.getHashes(5));
    }

    @Test
    void testGenerateHashesAsyncWithRepositoryError() {
        when(hashRepository.getNextRange(anyInt())).thenThrow(new RuntimeException("Database error"));

        assertThrows(HashGenerationException.class, () -> hashGenerator.generateHashesAsync());
    }

    @Test
    void testGenerateHashesAsyncWithEncoderError() {
        List<Long> range = Arrays.asList(1L, 2L, 3L);
        when(hashRepository.getNextRange(anyInt())).thenReturn(range);
        when(base62Encoder.encode(range)).thenThrow(new RuntimeException("Encoding error"));

        assertThrows(HashGenerationException.class, () -> hashGenerator.generateHashesAsync());
    }

    @Test
    void testGenerateHashesAsyncWithSaveError() {
        List<Long> range = Arrays.asList(1L, 2L, 3L);
        List<String> encodedHashes = Arrays.asList("hash1", "hash2", "hash3");
        when(hashRepository.getNextRange(anyInt())).thenReturn(range);
        when(base62Encoder.encode(range)).thenReturn(encodedHashes);
        when(hashRepository.saveAll(any())).thenThrow(new RuntimeException("Save error"));

        assertThrows(HashGenerationException.class, () -> hashGenerator.generateHashesAsync());
    }
} 