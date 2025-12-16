package faang.school.urlshortenerservice.hash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private AsyncHashProvider asyncHashProvider;

    private String anyString;
    private List<String> anyHashes;
    private CompletableFuture<List<String>> completableFutureOfAnyHashes;

    @BeforeEach
    public void setUp() throws Exception {
        int anyCacheCapacity = 5;
        int anyMinPercentFillingCacheForTest = 50;

        anyString = "anyString";
        anyHashes = List.of("firstAnyString", "secondAnyString", "thirdAnyString", "fourthAnyString", "fifthAnyString");
        completableFutureOfAnyHashes = CompletableFuture.completedFuture(anyHashes);

        Field cacheCapacity = HashCache.class.getDeclaredField("cacheCapacity");
        cacheCapacity.setAccessible(true);
        cacheCapacity.set(hashCache, anyCacheCapacity);

        Field minPercentFillingCache = HashCache.class.getDeclaredField("minPercentFillingCache");
        minPercentFillingCache.setAccessible(true);
        minPercentFillingCache.set(hashCache, anyMinPercentFillingCacheForTest);
    }

    @Test
    void fillCache_FillsSuccessfully() throws NoSuchFieldException {
        Field hashes = HashCache.class.getDeclaredField("hashes");
        hashes.setAccessible(true);
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(anyHashes);

        hashCache.fillCache();

        assertNotNull(hashes);
        assertEquals(5, anyHashes.size());
        assertTrue(anyHashes.containsAll(anyHashes));
        verify(hashGenerator, times(1)).getHashes(any(Integer.class));
    }

    @Test
    public void getFreeHash_CacheFillingIsInProgress() throws Exception {
        Field isCacheFillingInProgress = HashCache.class.getDeclaredField("isCacheFillingInProgress");
        isCacheFillingInProgress.setAccessible(true);
        isCacheFillingInProgress.set(hashCache, new AtomicBoolean(true));

        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(List.of(anyString));
        hashCache.fillCache();

        assertEquals(anyString, hashCache.getFreeHash());
        verify(asyncHashProvider, never()).getHashes(any(Integer.class));
    }

    @Test
    public void getFreeHash_CacheFillingIsNotInProgress() {
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(List.of(anyString));
        when(asyncHashProvider.getHashes(any(Integer.class))).thenReturn(completableFutureOfAnyHashes);
        hashCache.fillCache();

        assertEquals(anyString, hashCache.getFreeHash());
        verify(asyncHashProvider, times(1)).getHashes(any(Integer.class));
    }

    @Test
    public void getFreeHash_ReturnsHash() {
        when(hashGenerator.getHashes(any(Integer.class))).thenReturn(anyHashes);
        hashCache.fillCache();

        assertEquals(anyHashes.get(0), hashCache.getFreeHash());
        verify(asyncHashProvider, never()).getHashes(any(Integer.class));
    }
}
