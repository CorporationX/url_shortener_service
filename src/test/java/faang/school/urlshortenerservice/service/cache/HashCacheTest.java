package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private final int capacity = 10;
    private final Queue<String> queue = new ArrayBlockingQueue<>(capacity);
    private Field hashesField;

    @BeforeEach
    void setUp() throws Exception {
        Field capacityField = HashCache.class.getDeclaredField("capacity");
        capacityField.setAccessible(true);
        capacityField.set(hashCache, capacity);

        Field minLoadFactorField = HashCache.class.getDeclaredField("minLoadFactor");
        minLoadFactorField.setAccessible(true);
        int loadFactor = 20;
        minLoadFactorField.set(hashCache, loadFactor);

        hashesField = HashCache.class.getDeclaredField("hashes");
        hashesField.setAccessible(true);
        hashesField.set(hashCache, queue);

        Field runningField = HashCache.class.getDeclaredField("running");
        runningField.setAccessible(true);
        ((AtomicBoolean) runningField.get(hashCache)).set(false);
    }

    @Test
    void testInit() throws Exception {
        List<String> initHashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5", "hash6", "hash7", "hash8", "hash9", "hash10");
        when(hashGenerator.getHashes(capacity)).thenReturn(initHashes);

        hashCache.init();

        Queue<String> hashes = (Queue<String>) hashesField.get(hashCache);

        assertEquals(capacity, hashes.size());
        assertTrue(hashes.containsAll(initHashes));
    }

    @Test
    void testGetHash() {
        queue.addAll(List.of("hash1", "hash2", "hash3"));

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        assertEquals(2, queue.size());
        verify(hashGenerator, times(0)).getHashesAsync(anyLong());
    }

    @Test
    void testGetHashWithReplenishment() {
        queue.addAll(List.of("hash1", "hash2"));
        List<String> newHashes = List.of("hash4", "hash5", "hash6");
        int amount = capacity - queue.size();
        when(hashGenerator.getHashesAsync(amount)).thenReturn(CompletableFuture.completedFuture(newHashes));

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        assertEquals(4, queue.size());
        verify(hashGenerator, times(1)).getHashesAsync(amount);
    }
}