package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    private LocalCache localCache;

    private final int CAPACITY = 10000;
    private final int PERCENTAGE = 20;

    @BeforeEach
    void setUp() {
        localCache = new LocalCache(hashGenerator, CAPACITY, PERCENTAGE);

        Queue<String> newQueue = new ArrayBlockingQueue<>(CAPACITY);
        ReflectionTestUtils.setField(localCache, "hashes", newQueue);

        try{
            var field = LocalCache.class.getDeclaredField( "hashes");
            field.setAccessible(true);
            field.set(localCache, newQueue);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        List<String> initialHashes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            initialHashes.add("hash" + i);
        }
        when(hashGenerator.getHashes(CAPACITY)).thenReturn(initialHashes);
        lenient().when(hashGenerator.getHashesAsync(CAPACITY))
                .thenReturn(CompletableFuture.completedFuture(List.of("asyncHash1", "asyncHash2")));

        localCache.init();
    }

    @Test
    void testInit_PopulatesCache() {
        verify(hashGenerator, times(1)).getHashes(CAPACITY);
        String hash = localCache.getHash();
        assertNotNull(hash);
    }

    @Test
    void testGetHash_ReturnsHash() {
        String hash = localCache.getHash();
        assertNotNull(hash);
    }

    @Test
    void testGetHash_TriggersAsyncFill_WhenBelowThreshold() {
        for (int i = 0; i < 3; i++) {
            localCache.getHash();
        }
        localCache.getHash();
        verify(hashGenerator, atLeastOnce()).getHashesAsync(CAPACITY);
    }

    @Test
    void testGetHash_AsyncFillExceptionHandled() {
        CompletableFuture<List<String>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Async error"));
        when(hashGenerator.getHashesAsync(CAPACITY)).thenReturn(failedFuture);

        for (int i = 0; i < 7; i++) {
            localCache.getHash();
        }

        localCache.getHash();

        verify(hashGenerator, atLeastOnce()).getHashesAsync(CAPACITY);
        var atomicFilling = (java.util.concurrent.atomic.AtomicBoolean)
                ReflectionTestUtils.getField(localCache, "filling");
        assertFalse(atomicFilling.get());
    }

    @Test
    void testGetHash_NoAsyncFill_WhenNoSpaceToFill() {
        ReflectionTestUtils.setField(localCache, "fillPercent", 110);
        int newCapacity = 3;
        ReflectionTestUtils.setField(localCache, "capacity", newCapacity);
        Queue<String> fullQueue = new ArrayBlockingQueue<>(newCapacity);
        fullQueue.add("fullHash1");
        fullQueue.add("fullHash2");
        fullQueue.add("fullHash3");
        ReflectionTestUtils.setField(localCache, "hashes", fullQueue);

        String hash = localCache.getHash();
        assertEquals("fullHash1", hash);
        verify(hashGenerator, never()).getHashesAsync(anyInt());
    }

    @Test
    void testGetHash_DoesNotTriggerAsyncFill_WhenAlreadyFilling() {
        var atomicFilling = (java.util.concurrent.atomic.AtomicBoolean)
                ReflectionTestUtils.getField(localCache, "filling");
        atomicFilling.set(true);

        for (int i = 0; i < 7; i++) {
            localCache.getHash();
        }
        localCache.getHash();
        verify(hashGenerator, never()).getHashesAsync(10);

        atomicFilling.set(false);
    }
}

