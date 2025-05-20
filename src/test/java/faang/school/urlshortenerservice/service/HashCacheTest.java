package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private ExecutorService executorService;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private BlockingDeque<String> availableHashes;
    private AtomicBoolean refillInProgress;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "cacheSize", 100);
        ReflectionTestUtils.setField(hashCache, "refillPercent", 20);

        availableHashes = (BlockingDeque<String>) ReflectionTestUtils.getField(hashCache, "availableHashes");
        refillInProgress = (AtomicBoolean) ReflectionTestUtils.getField(hashCache, "refillInProgress");
    }

    @Test
    void givenEmptyCache_whenGetHashC_thenReturnsEmptyAndSchedulesRefill() {
        Optional<String> result = hashCache.getHash();

        assertFalse(result.isPresent());
        verify(executorService, times(1)).submit(any(Runnable.class)); // scheduleRefill вызывается
        assertTrue(refillInProgress.get());
    }

    @Test
    void givenCacheBelowThreshold_whenGetHash_thenSchedulesRefill() {
        for (int i = 0; i < 19; i++) {
            availableHashes.add("hash" + i);
        }

        hashCache.getHash();

        verify(executorService, times(1)).submit(any(Runnable.class));
        assertTrue(refillInProgress.get());
    }

    @Test
    void givenCacheBelowThreshold_whenShouldRefill_thenReturnsTrue() {
        for (int i = 0; i < 19; i++) {
            availableHashes.add("hash" + i);
        }

        boolean shouldRefill = ReflectionTestUtils.invokeMethod(hashCache, "shouldRefill");

        assertTrue(shouldRefill);
    }

    @Test
    void givenCacheAboveThreshold_whenShouldRefill_thenReturnsFalse() {
        for (int i = 0; i < 21; i++) {
            availableHashes.add("hash" + i);
        }

        boolean shouldRefill = ReflectionTestUtils.invokeMethod(hashCache, "shouldRefill");

        assertFalse(shouldRefill);
    }

    @Test
    void givenRefillInProgress_whenScheduleRefill_thenDoesNotSubmitTask() {
        refillInProgress.set(true);

        ReflectionTestUtils.invokeMethod(hashCache, "scheduleRefill");

        verifyNoInteractions(executorService);
        assertTrue(refillInProgress.get());
    }

    @Test
    void givenEmptyCache_whenRefillCache_thenGeneratesAndAddsHashes() {
        List<String> newHashes = List.of("hash1", "hash2", "hash3");
        when(hashRepository.getHashBatch(100)).thenReturn(newHashes);
        doNothing().when(hashGenerator).generateBatch();

        ReflectionTestUtils.invokeMethod(hashCache, "refillCache");

        verify(hashGenerator, times(1)).generateBatch();
        verify(hashRepository, times(1)).getHashBatch(100);
        assertEquals(3, availableHashes.size());
        assertTrue(availableHashes.containsAll(newHashes));
        assertFalse(refillInProgress.get());
    }

    @Test
    void givenEmptyCache_whenRefillCacheCalledWithEmptyBatch_thenKeepsCacheEmpty() {
        when(hashRepository.getHashBatch(100)).thenReturn(List.of());
        doNothing().when(hashGenerator).generateBatch();

        ReflectionTestUtils.invokeMethod(hashCache, "refillCache");

        verify(hashGenerator, times(1)).generateBatch();
        verify(hashRepository, times(1)).getHashBatch(100);
        assertEquals(0, availableHashes.size());
        assertFalse(refillInProgress.get());
    }
}