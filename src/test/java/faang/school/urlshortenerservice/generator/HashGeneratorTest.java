package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    private final int maxRange = 100;
    private final List<Long> testNumbers = List.of(1L, 2L, 3L);
    private final List<String> testHashes = List.of("a", "b", "c");
    private final List<Hash> testHashEntities = List.of(
            new Hash(1L, "a"),
            new Hash(2L, "b"),
            new Hash(3L, "c")
    );

    @BeforeEach
    void setUp() {
        hashGenerator.setMaxRange(maxRange);
    }

    @Test
    void testGenerateHashes_Success() {
        when(hashRepository.getNextRange(maxRange)).thenReturn(testNumbers);
        when(base62Encoder.encode(testNumbers)).thenReturn(testHashes);

        hashGenerator.generateHashes();

        verify(hashRepository).getNextRange(maxRange);
        verify(base62Encoder).encode(testNumbers);
        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void testGenerateHashes_EmptyRange() {
        when(hashRepository.getNextRange(maxRange)).thenReturn(List.of());

        hashGenerator.generateHashes();

        verify(hashRepository).getNextRange(maxRange);
        verifyNoInteractions(base62Encoder);
        verify(hashRepository, never()).saveAll(anyList());
    }

    @Test
    void testGetHashes_EnoughHashesAvailable() {
        long amount = 2;
        when(hashRepository.findAndDelete(amount)).thenReturn(testHashEntities.subList(0, 2));

        List<String> result = hashGenerator.getHashes(amount);

        assertEquals(2, result.size());
        verify(hashRepository).findAndDelete(amount);
        verifyNoMoreInteractions(hashRepository);
    }

    @Test
    void testGetHashesAsync_Success() throws ExecutionException, InterruptedException {
        long amount = 2;
        when(hashRepository.findAndDelete(amount)).thenReturn(testHashEntities.subList(0, 2));

        CompletableFuture<List<String>> future = hashGenerator.getHashesAsync(amount);
        List<String> result = future.get();

        assertEquals(2, result.size());
        verify(hashRepository).findAndDelete(amount);
    }

    @Test
    void testSaveHashes_Success() {
        hashGenerator.saveHashes(testHashes);

        verify(hashRepository).saveAll(anyList());
    }

    @Test
    void testSaveHashes_EmptyList() {
        hashGenerator.saveHashes(List.of());

        verify(hashRepository, never()).saveAll(anyList());
    }

    @Test
    void testSaveHashes_NullList() {
        hashGenerator.saveHashes(null);

        verify(hashRepository, never()).saveAll(anyList());
    }

    @Test
    void testGetHashes_ZeroAmount() {
        List<String> result = hashGenerator.getHashes(0);

        assertTrue(result.isEmpty());
        verifyNoInteractions(hashRepository);
    }

    @Test
    void testGetHashes_NegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> hashGenerator.getHashes(-1));
        verifyNoInteractions(hashRepository);
    }

    @Test
    void testGenerateHashes_RepositoryThrowsException() {
        when(hashRepository.getNextRange(maxRange)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> hashGenerator.generateHashes());
        verifyNoInteractions(base62Encoder);
    }

    @Test
    void testGetHashes_SecondAttemptAlsoFails() {
        long amount = 5;
        when(hashRepository.findAndDelete(amount)).thenReturn(List.of());
        when(hashRepository.getNextRange(maxRange)).thenReturn(List.of());

        List<String> result = hashGenerator.getHashes(amount);

        assertTrue(result.isEmpty());
        verify(hashRepository, times(2)).findAndDelete(anyLong());
    }
}