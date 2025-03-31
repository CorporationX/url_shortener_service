package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.FreeHash;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashFacadeTest {

    @Mock
    private FreeHashGenerator freeHashGenerator;
    @Mock
    private LocalCacheService localCacheService;
    @Mock
    private FreeHashRepository freeHashRepository;
    @Mock
    private Executor executor;
    @InjectMocks
    private HashFacade hashFacade;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashFacade, "maxDbCapacity", 1000L);
        ReflectionTestUtils.setField(hashFacade, "maxCacheCapacity", 100);
        ReflectionTestUtils.setField(hashFacade, "refillThresholdPercent", 20);
    }

    @Test
    void testGetAvailableHash_TriggersRefillAndReturnsHash() {
        FreeHash expectedHash = new FreeHash();
        when(localCacheService.getAvailableHash()).thenReturn(expectedHash);

        FreeHash result = hashFacade.getAvailableHash();

        verify(localCacheService).getAvailableHash();
        verify(executor).execute(runnableCaptor.capture());
        assertNotNull(runnableCaptor.getValue());
        assertEquals(expectedHash, result);
    }

    @Test
    void testTriggerAsyncRefill_WhenCacheBelowThreshold_TriggersRefill() {
        int currentCacheSize = 15;
        when(localCacheService.getCacheSize()).thenReturn(currentCacheSize);

        hashFacade.triggerAsyncRefill();

        verify(executor).execute(any(Runnable.class));
    }

    @Test
    void testTriggerAsyncRefill_WhenCacheAboveThreshold_DoesNothing() {
        int currentCacheSize = 25; // 25 >= 20 (20% of 100)
        when(localCacheService.getCacheSize()).thenReturn(currentCacheSize);

        hashFacade.triggerAsyncRefill();

        verifyNoInteractions(executor);
    }

    @Test
    void testAsyncRefill_WhenLockIsFree_RefillsCache() {
        AtomicBoolean lock = (AtomicBoolean) ReflectionTestUtils.getField(hashFacade, "lock");
        lock.set(false);
        int toRefill = 50;

        hashFacade.asyncRefill(50);

        verify(localCacheService).refillCache(toRefill);
        assertFalse(lock.get());
    }

    @Test
    void testAsyncRefill_WhenLockIsAcquired_DoesNothing() {
        AtomicBoolean lock = (AtomicBoolean) ReflectionTestUtils.getField(hashFacade, "lock");
        lock.set(true);

        hashFacade.asyncRefill(50);

        verifyNoInteractions(localCacheService);
        assertTrue(lock.get());
    }

    @Test
    void testWarmUpCache_RefillsDbAndCache() {
        when(freeHashRepository.count()).thenReturn(500L);
        List<FreeHash> hashes = List.of(new FreeHash(), new FreeHash());
        when(freeHashRepository.deleteAndReturnFreeHashes(100)).thenReturn(hashes);

        hashFacade.warmUpCache();

        verify(freeHashGenerator).refillDb(500L);
        verify(localCacheService).addAll(hashes);
    }
}