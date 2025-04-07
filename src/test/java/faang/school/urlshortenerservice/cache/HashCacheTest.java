package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.generator.HashAsyncService;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    private final static int TEST_CAPACITY = 10;
    private final static int TEST_MAX_RANGE = 100;
    private final static long TEST_PERCENT_TO_FILL = 30;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashAsyncService hashAsyncService;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", TEST_CAPACITY);
        ReflectionTestUtils.setField(hashCache, "maxRange", TEST_MAX_RANGE);
        ReflectionTestUtils.setField(hashCache, "percentToFill", TEST_PERCENT_TO_FILL);

        Queue<Hash> testCache = new ArrayBlockingQueue<>(TEST_CAPACITY);
        for (int i = 0; i < TEST_CAPACITY; i++) {
            testCache.add(new Hash("hash" + i));
        }
        ReflectionTestUtils.setField(hashCache, "cache", testCache);
    }

    @Test
    void initCache_shouldInitializeCacheWithHashes() {
        List<Hash> mockHashes = List.of(new Hash("h1"), new Hash("h2"));
        when(hashGenerator.getHashes(TEST_CAPACITY)).thenReturn(mockHashes);

        hashCache.initCache();

        Queue<Hash> cache = (Queue<Hash>) ReflectionTestUtils.getField(hashCache, "cache");
        assertEquals(2, cache.size());
        verify(hashGenerator, times(1)).getHashes(TEST_CAPACITY);
    }

    @Test
    void getHash_shouldReturnHashWhenCacheNotEmpty() {
        Hash result = hashCache.getHash();
        assertNotNull(result);
        assertEquals("hash0", result.getHash());
    }


    @Test
    void getHash_shouldTriggerAsyncRefillWhenBelowThreshold() {
        List<Hash> newHashes = List.of(new Hash("new1"), new Hash("new2"));
        when(hashAsyncService.getHashesAsync(TEST_MAX_RANGE))
                .thenReturn(CompletableFuture.completedFuture(newHashes));

        for (int i = 0; i < 9; i++) {
            hashCache.getHash();
        }

        verify(hashAsyncService, times(1)).getHashesAsync(TEST_MAX_RANGE);

        AtomicBoolean isFilling = (AtomicBoolean) ReflectionTestUtils.getField(hashCache, "isFilling");
        assertFalse(isFilling.get());
    }

    @Test
    void getHash_shouldNotTriggerRefillWhenAboveThreshold() {
        for (int i = 0; i < 6; i++) {
            hashCache.getHash();
        }

        verify(hashAsyncService, never()).getHashesAsync(anyInt());
    }

    @Test
    void getHash_shouldNotTriggerMultipleRefills() {
        CompletableFuture<List<Hash>> future = new CompletableFuture<>();
        when(hashAsyncService.getHashesAsync(TEST_MAX_RANGE)).thenReturn(future);

        for (int i = 0; i < 8; i++) {
            hashCache.getHash();
        }

        for (int i = 0; i < 3; i++) {
            hashCache.getHash();
        }

        verify(hashAsyncService, times(1)).getHashesAsync(TEST_MAX_RANGE);
    }
}