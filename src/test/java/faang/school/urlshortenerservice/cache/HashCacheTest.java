package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    private int capacity;
    private ArrayBlockingQueue<Hash> cache;
    @Mock
    private HashGenerator hashGenerator;

    private HashCache hashCache;
    private List<Hash> hashes;


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        capacity = 10;
        int minPercentageFilling = 20;
        hashCache = new HashCache(capacity, minPercentageFilling, hashGenerator);
        hashes = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            hashes.add(new Hash());
        }

        cache = spy(new ArrayBlockingQueue<>(capacity));
        Field reflectedCache = HashCache.class.getDeclaredField("cache");
        reflectedCache.setAccessible(true);
        reflectedCache.set(hashCache, cache);
    }

    @Test
    void testInit() {
        when(hashGenerator.getHashes(capacity)).thenReturn(hashes);

        hashCache.init();
        verify(hashGenerator, times(1)).getHashes(capacity);
        verify(cache, times(1)).addAll(hashes);
    }

    @Test
    void testGetHashWhenHashesEnough() {
        cache.addAll(hashes);

        Hash actual = hashCache.getHash();
        verify(cache, times(1)).poll();
        verify(hashGenerator, times(0)).getHashesAsync(anyInt());
        assertEquals(hashes.get(0), actual);
    }

    @Test
    void testGetHashWhenHashesNotEnough() {
        cache.add(hashes.get(0));
        List<Hash> extraHashes = new ArrayList<>();
        for (int i = 1; i < hashes.size(); i++) {
            extraHashes.add(hashes.get(i));
        }
        int extraHashesSize = extraHashes.size();
        CompletableFuture<List<Hash>> generatedHashes = CompletableFuture.completedFuture(extraHashes);
        when(hashGenerator.getHashesAsync(extraHashesSize)).thenReturn(generatedHashes);

        Hash actual = hashCache.getHash();
        verify(hashGenerator, times(1)).getHashesAsync(extraHashesSize);
        verify(cache, times(1)).addAll(extraHashes);
        verify(cache, times(1)).poll();
        assertEquals(hashes.get(0), actual);
    }
}
