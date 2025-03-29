package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGeneratorService hashGeneratorService;

    @Mock
    private ExecutorService hashesExecutorService;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "cacheCapacity", 10);
        ReflectionTestUtils.setField(hashCache, "hashesCache", new ArrayBlockingQueue<>(10));
    }

    @Test
    void testInit() {
        // Given
        List<Hash> mockHashes = createMockHashes(10);
        when(hashRepository.findAndDelete(10)).thenReturn(mockHashes);

        // When
        hashCache.init();

        // Then
        assertEquals(10, getCacheQueue().size());
        verify(hashRepository).findAndDelete(10);
    }

    @Test
    void testGetCachedHash() {
        // Given
        List<Hash> mockHashes = createMockHashes(10);
        when(hashRepository.findAndDelete(10)).thenReturn(mockHashes);
        hashCache.init();

        // When
        String hash = hashCache.getCachedHash();

        // Then
        assertNotNull(hash);
        assertEquals("hash1", hash);
        assertEquals(9, getCacheQueue().size());
    }

    @Test
    void testGetCachedHashTriggersRefillWhenLow() {
        // Given
        List<Hash> mockHashes = createMockHashes(3);  // Only 3 hashes (30% of capacity)
        when(hashRepository.findAndDelete(10)).thenReturn(mockHashes);
        hashCache.init();

        // Consume 2 hashes to reach the threshold (10% of capacity)
        hashCache.getCachedHash();
        hashCache.getCachedHash();
        reset(hashesExecutorService);

        // When
        hashCache.getCachedHash();

        // Then
        verify(hashesExecutorService).execute(any(Runnable.class));
    }

    @Test
    void testGetHashes() {
        // Given
        List<Hash> mockHashes = createMockHashes(5);
        when(hashRepository.findAndDelete(5)).thenReturn(mockHashes);

        // When
        List<String> result = hashCache.getHashes(5);

        // Then
        assertEquals(5, result.size());
        assertEquals("hash1", result.get(0));
        assertEquals("hash5", result.get(4));
        verify(hashRepository).findAndDelete(5);
        verify(hashGeneratorService, never()).generateBatch(anyInt());
    }

    @Test
    void testGetHashesWithGeneration() {
        // Given
        List<Hash> firstBatch = createMockHashes(3);
        List<Hash> secondBatch = createMockHashes(2, 4);
        when(hashRepository.findAndDelete(5)).thenReturn(firstBatch);
        when(hashRepository.findAndDelete(2)).thenReturn(secondBatch);

        // When
        List<String> result = hashCache.getHashes(5);

        // Then
        assertEquals(5, result.size());
        verify(hashRepository).findAndDelete(5);
        verify(hashGeneratorService).generateBatch(2);
        verify(hashRepository).findAndDelete(2);
    }


    private Queue<?> getCacheQueue() {
        return (Queue<?>) ReflectionTestUtils.getField(hashCache, "hashesCache");
    }

    private List<Hash> createMockHashes(int count) {
        return createMockHashes(count, 1);
    }

    private List<Hash> createMockHashes(int count, int startIndex) {
        List<Hash> hashes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Hash hash = new Hash("hash" + (i + startIndex));
            hashes.add(hash);
        }
        return hashes;
    }
}