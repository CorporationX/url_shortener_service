package faang.school.urlshortenerservice.cache;


import faang.school.urlshortenerservice.encoder.BaseEncoder;
import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private BaseEncoder baseEncoder;

    @InjectMocks
    private HashCache hashCache;

    private List<String> hashes;
    private Queue<String> queue;
    private AtomicBoolean isGenerating;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "cacheSize", 10);
        ReflectionTestUtils.setField(hashCache, "minFillPercent", 0.2);

        queue = new ArrayBlockingQueue<>(100);
        hashes = List.of("hash1", "hash2", "hash3");
        queue.addAll(hashes);
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        isGenerating = new AtomicBoolean(false);
        ReflectionTestUtils.setField(hashCache, "isGenerating", isGenerating);
    }

    @Test
    void testInitCache_Success() {
        when(hashGenerator.getHashes()).thenReturn(hashes);
        hashCache.initCash();

        verify(hashGenerator).generateHash();
        verify(hashGenerator).getHashes();

        assertNotNull(queue);
        assertEquals(hashes.size(), queue.size());
        assertTrue(queue.containsAll(hashes));
    }

    @Test
    void testInitCache_EmptyList() {
        queue.clear();
        when(hashGenerator.getHashes()).thenReturn(Collections.emptyList());
        hashCache.initCash();

        verify(hashGenerator).generateHash();
        verify(hashGenerator).getHashes();

        assertTrue(queue.isEmpty());
    }

    @Test
    void testGetHash_CacheIsFull() {
        String result = hashCache.getHash();

        assertEquals("hash1", result);
    }

    @Test
    void testGetHash_CacheIsLowTriggerGeneration() {
        queue.clear();
        queue.add("hash");
        isGenerating.set(false);
        CompletableFuture<List<String>> futureHashes = CompletableFuture
                .completedFuture(List.of("hash1", "hash2", "hash3"));
        when(hashGenerator.getHashesAsync()).thenReturn(futureHashes);

        String result = hashCache.getHash();

        assertEquals("hash", result);
        verify(hashGenerator).getHashesAsync();
        assertFalse(isGenerating.get());
    }

    @Test
    void testGetHash_CacheIsLowGenerationInProgress() {
        queue.clear();
        queue.add("hash");
        isGenerating.set(true);
        CompletableFuture<List<String>> futureHashes = CompletableFuture
                .completedFuture(List.of("hash1", "hash2", "hash3"));
        lenient().when(hashGenerator.getHashesAsync()).thenReturn(futureHashes);

        String hash = hashCache.getHash();

        assertEquals("hash", hash);
        assertTrue(isGenerating.get());
    }

    @Test
    void testGetHash_CacheIsEmptyException() {
        queue.clear();
        CompletableFuture<List<String>> futureHashes = CompletableFuture
                .completedFuture(Collections.emptyList());
        lenient().when(hashGenerator.getHashesAsync()).thenReturn(futureHashes);

        HashCacheException exception = assertThrows(HashCacheException.class,
                () -> hashCache.getHash());

        assertEquals("Свободный хэш отсутствует", exception.getMessage());
    }
}
