package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashGenerator;
import faang.school.urlshortenerservice.config.app.HashCacheConfig;
import faang.school.urlshortenerservice.exception.NoHashAvailableException;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashCacheConfig config;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService hashCacheExecutor;

    @InjectMocks
    private HashCache hashCache;

    private static final String HASH = "abc123";
    private static final int MAX_SIZE = 100;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(config, hashRepository, hashGenerator, hashCacheExecutor);
    }

    @Test
    void testGetHashFromCacheSuccess() {
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < MAX_SIZE; i++) {
            hashes.add(HASH + i);
        }
        when(hashRepository.getHashBatch()).thenReturn(hashes);

        hashCache.refillCache();

        String result = hashCache.getHash();

        assertEquals(HASH + "0", result);
        verify(hashCacheExecutor, never()).submit(any(Runnable.class));
        assertEquals(MAX_SIZE - 1, hashCache.getCacheSize());
    }

    @Test
    void testGetHashFromDatabaseSuccess() {
        when(config.getMaxSize()).thenReturn(MAX_SIZE);
        when(config.getRefillThreshold()).thenReturn(50);
        when(hashRepository.getHashBatch()).thenReturn(List.of(HASH));
        hashCache.clearCache();

        String result = hashCache.getHash();

        assertEquals(HASH, result);
        verify(hashRepository).getHashBatch();
        verify(hashCacheExecutor, times(1)).submit(any(Runnable.class));
        assertEquals(0, hashCache.getCacheSize());
    }

    @Test
    void testGetHashRefillTriggeredSuccess() {
        when(config.getMaxSize()).thenReturn(MAX_SIZE);
        when(config.getRefillThreshold()).thenReturn(50);
        List<String> initialBatch = List.of(HASH);
        List<String> refillBatch = new ArrayList<>();
        for (int i = 0; i < MAX_SIZE - 1; i++) {
            refillBatch.add("hash" + i);
        }

        when(hashRepository.getHashBatch())
                .thenReturn(initialBatch)
                .thenReturn(refillBatch);

        when(hashGenerator.generateHashes(anyInt()))
                .thenReturn(CompletableFuture.completedFuture(null));

        doAnswer(invocation -> {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                hashCache.refillCache();
            }).start();
            return null;
        }).when(hashCacheExecutor).submit(any(Runnable.class));

        hashCache.addToCache(HASH);

        String result = hashCache.getHash();

        await().atMost(1, TimeUnit.SECONDS).until(() -> hashCache.getCacheSize() == MAX_SIZE);

        assertEquals(HASH, result);
        verify(hashCacheExecutor, times(1)).submit(any(Runnable.class));
        assertEquals(MAX_SIZE, hashCache.getCacheSize());
    }

    @Test
    void testGetHashNoHashesThrowsException() {
        when(config.getMaxSize()).thenReturn(MAX_SIZE);
        hashCache.clearCache();
        when(hashRepository.getHashBatch()).thenReturn(Collections.emptyList());
        when(hashGenerator.generateHashes(anyInt())).thenReturn(CompletableFuture.completedFuture(null));
        doAnswer(invocation -> {
            hashCache.refillCache();
            return null;
        }).when(hashCacheExecutor).submit(any(Runnable.class));

        NoHashAvailableException exception = assertThrows(NoHashAvailableException.class, () -> hashCache.getHash());
        assertEquals("Failed to fill cache to maxSize, current size: 0", exception.getMessage());
        verify(hashCacheExecutor).submit(any(Runnable.class));
    }


    @Test
    void testRefillCacheSuccess() throws InterruptedException {
        when(config.getMaxSize()).thenReturn(MAX_SIZE);
        List<String> initialBatch = List.of(HASH);
        List<String> generatedBatch = new ArrayList<>();
        for (int i = 0; i < MAX_SIZE - 1; i++) {
            generatedBatch.add("hash" + i);
        }

        when(hashRepository.getHashBatch())
                .thenReturn(initialBatch)
                .thenReturn(generatedBatch);
        when(hashGenerator.generateHashes(anyInt())).thenReturn(CompletableFuture.completedFuture(null));

        hashCache.refillCache();

        assertEquals(MAX_SIZE, hashCache.getCacheSize());
        verify(hashRepository, atLeastOnce()).getHashBatch();
        verify(hashGenerator).generateHashes(anyInt());
        assertFalse(hashCache.isRefilling());
    }

    @Test
    void testRefillCacheNoHashesAfterGenerationThrowsException() {
        when(config.getMaxSize()).thenReturn(MAX_SIZE);
        when(hashRepository.getHashBatch()).thenReturn(Collections.emptyList());
        when(hashGenerator.generateHashes(anyInt())).thenReturn(CompletableFuture.completedFuture(null));

        NoHashAvailableException exception = assertThrows(NoHashAvailableException.class, () -> hashCache.refillCache());
        assertTrue(exception.getMessage().contains("Failed to fill cache to maxSize"));
        verify(hashGenerator).generateHashes(anyInt());
        assertFalse(hashCache.isRefilling());
    }

    @Test
    void testPopulateDatabaseAsyncSuccess() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        when(hashGenerator.generateHashes(anyInt())).thenReturn(
                CompletableFuture.supplyAsync(() -> {
                    latch.countDown();
                    return null;
                }));

        hashCache.populateDatabaseAsync();

        latch.await(2, TimeUnit.SECONDS);
        verify(hashGenerator).generateHashes(anyInt());
    }
}