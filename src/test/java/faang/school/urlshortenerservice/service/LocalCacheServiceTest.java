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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalCacheServiceTest {

    @Mock
    private FreeHashGenerator freeHashGenerator;
    @Mock
    private FreeHashRepository freeHashRepository;
    @Mock
    @Qualifier("hashServiceExecutor")
    private Executor executor;
    @InjectMocks
    private LocalCacheService localCacheService;
    private Queue<FreeHash> freeHashesCache;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(localCacheService, "maxDbCapacity", 1000L);
        freeHashesCache = (Queue<FreeHash>) ReflectionTestUtils.getField(localCacheService, "FREE_HASHES_CACHE");
        freeHashesCache.clear();
    }

    @Test
    void testGetAvailableHash_RemovesHashFromCache() {
        FreeHash hash = new FreeHash();
        freeHashesCache.add(hash);

        FreeHash result = localCacheService.getAvailableHash();

        assertEquals(hash, result);
        assertTrue(freeHashesCache.isEmpty());
    }

    @Test
    void testGetCacheSize_ReturnsCorrectSize() {
        freeHashesCache.add(new FreeHash());
        freeHashesCache.add(new FreeHash());

        int size = localCacheService.getCacheSize();

        assertEquals(2, size);
    }

    @Test
    void testRefillCache_AddsHashesFromDbAndRefillsDb() {
        List<FreeHash> dbHashes = List.of(new FreeHash(), new FreeHash());
        when(freeHashRepository.deleteAndReturnFreeHashes(10)).thenReturn(dbHashes);
        when(freeHashRepository.count()).thenReturn(900L);

        localCacheService.refillCache(10);

        assertEquals(2, freeHashesCache.size());
        verify(executor).execute(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(freeHashGenerator).refillDb(100L);
    }

    @Test
    void testAddAll_AddsHashesToCache() {
        List<FreeHash> hashes = List.of(new FreeHash(), new FreeHash());

        localCacheService.addAll(hashes);

        assertEquals(2, freeHashesCache.size());
    }
}