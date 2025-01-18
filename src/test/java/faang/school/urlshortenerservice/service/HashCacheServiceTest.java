package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.UrlShortenerProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.LocalCacheException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheServiceTest {

    @Mock
    private HashService hashService;

    @Mock
    private ThreadPoolTaskExecutor localCacheExecutor;

    @Mock
    private ArrayBlockingQueue<Hash> localCache;

    private UrlShortenerProperties urlShortenerProperties;

    private CompletableFuture<List<Hash>> completableFuture;

    private HashCacheService hashCacheService;

    @BeforeEach
    void setUp() {
        urlShortenerProperties = UrlShortenerProperties.builder()
                .localCacheCapacity(10)
                .localCacheThresholdRatio(0.5)
                .build();
        hashCacheService = new HashCacheService(localCache, hashService, localCacheExecutor, urlShortenerProperties);
    }

    @Test
    @DisplayName("Test adding hash to local hash - capacity below threshold - upload is needed")
    void test_addHashToLocalCacheIfNecessary_WhenCapacityBelowThreshold_Success() {

        when(localCache.size()).thenReturn(3);

        hashCacheService.addHashToLocalCacheIfNecessary();

        verify(localCacheExecutor, times(1)).execute(any());
    }

    @Test
    @DisplayName("Test adding hash to local hash - capacity below threshold - upload in progress")
    void test_addHashToLocalCacheIfNecessary_WhenCapacityBelowThresholdAndUploadInProgress_NothingHappens() {
        ReflectionTestUtils.setField(hashCacheService, "uploadInProgressFlag", new AtomicBoolean(true));

        when(localCache.size()).thenReturn(4);

        hashCacheService.addHashToLocalCacheIfNecessary();

        verify(localCacheExecutor, never()).execute(any());
    }

    @Test
    @DisplayName("Test adding hash to local hash - capacity above threshold - upload not needed")
    void test_addHashToLocalCacheIfNecessary_WhenCapacityAboveThreshold_UploadNotNeeded() {

        when(localCache.size()).thenReturn(6);

        hashCacheService.addHashToLocalCacheIfNecessary();

        verify(localCacheExecutor, never()).execute(any());
    }

    @Test
    @DisplayName("Test upload hash from database to local cache - success")
    void test_uploadHashFromDatabaseToLocalCache_Success() {
        List<Hash> hashes = Arrays.asList(new Hash(), new Hash(), new Hash());
        completableFuture = CompletableFuture.completedFuture(hashes);

        when(hashService.getHashesFromDatabase()).thenReturn(completableFuture);

        hashCacheService.uploadHashFromDatabaseToLocalCache();

        verify(hashService, times(1)).getHashesFromDatabase();
        verify(localCache, times(1)).addAll(hashes);
        verify(hashService,times(1)).uploadHashInDatabaseIfNecessary();
    }

    @Test
    @DisplayName("Test upload hash from database to local cache - error during download from database")
    void test_uploadHashFromDatabaseToLocalCache_ThrowsInterruptedException() throws Exception {
        completableFuture = mock(CompletableFuture.class);

        when(hashService.getHashesFromDatabase()).thenReturn(completableFuture);

        when(completableFuture.get()).thenThrow(new InterruptedException("Some error"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                hashCacheService.uploadHashFromDatabaseToLocalCache());

        verify(hashService, times(1)).getHashesFromDatabase();
        verify(localCache, never()).addAll(any());
        verify(hashService,never()).uploadHashInDatabaseIfNecessary();

        assertTrue(ex.getMessage().contains("Error during hash download from database. Error:"));
    }

    @Test
    @DisplayName("Test get hash from cache success")
    void test_getHashFromCache_success() {
        Hash hash = new Hash("Test hash");
        when(localCache.poll()).thenReturn(hash);
        when(localCache.size()).thenReturn(10);

        String result = hashCacheService.getHashFromCache();

        verify(localCacheExecutor, never()).execute(any());

        assertNotNull(result);
        assertEquals(hash.getHash(), result);
    }

    @Test
    @DisplayName("Test get hash from cache throws exception when cache is empty")
    void test_getHashFromCache_WhenCacheIsEmpty_ThenThrowsException() {
        localCache = new ArrayBlockingQueue<>(urlShortenerProperties.localCacheCapacity());

        LocalCacheException ex = assertThrows(LocalCacheException.class, () -> hashCacheService.getHashFromCache());
        assertEquals("Unable to provide hash for short URL", ex.getMessage());

        verify(localCacheExecutor, never()).execute(any());
    }
}