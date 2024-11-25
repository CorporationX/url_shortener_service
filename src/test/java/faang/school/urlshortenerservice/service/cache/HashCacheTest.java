package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
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

    @Mock
    private HashRepository hashRepository;

    private HashCache hashCache;

    private final int capacity = 10;
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();
    private Field hashesField;

    @Test
    @SuppressWarnings("unchecked")
    void testInitWhenNotEnoughHashes() throws Exception {
        List<String> initHashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5", "hash6", "hash7", "hash8", "hash9", "hash10");
        when(hashRepository.count()).thenReturn(5L);
        when(hashGenerator.generateHashes(capacity)).thenReturn(initHashes);
        setUp();

        Queue<String> hashes = (Queue<String>) hashesField.get(hashCache);

        assertEquals(capacity, hashes.size());
        assertTrue(hashes.containsAll(initHashes));
        verify(hashGenerator).generateHashes(capacity);
        verify(hashGenerator).generateHashesAsync();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testInitWhenEnoughHashes() throws Exception {
        List<String> initHashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5", "hash6", "hash7", "hash8", "hash9", "hash10");
        when(hashRepository.count()).thenReturn(1000L);
        when(hashGenerator.getHashes(capacity)).thenReturn(initHashes);
        setUp();

        Queue<String> hashes = (Queue<String>) hashesField.get(hashCache);

        assertEquals(capacity, hashes.size());
        assertTrue(hashes.containsAll(initHashes));
        verify(hashGenerator, times(1)).getHashes(capacity);
    }

    @Test
    void testGetHash() throws Exception {
        setUp();
        hashesField.set(hashCache, hashes);
        hashes.addAll(List.of("hash1", "hash2", "hash3"));

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        assertEquals(2, hashes.size());
        verify(hashGenerator, times(0)).getHashesAsync(anyLong());
    }

    @Test
    void testGetHashWithReplenishment() throws Exception {
        setUp();
        hashesField.set(hashCache, hashes);
        hashes.addAll(List.of("hash1", "hash2"));
        List<String> newHashes = List.of("hash4", "hash5", "hash6");
        int amount = capacity - hashes.size();
        when(hashGenerator.getHashesAsync(amount)).thenReturn(CompletableFuture.completedFuture(newHashes));

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        assertEquals(4, hashes.size());
        verify(hashGenerator, times(1)).getHashesAsync(amount);
    }

    private void setUp() throws Exception {

        int minLoadFactor = 20;
        hashCache = new HashCache(hashGenerator, hashRepository, capacity, minLoadFactor);

        hashesField = HashCache.class.getDeclaredField("hashes");
        hashesField.setAccessible(true);

        Field runningField = HashCache.class.getDeclaredField("running");
        runningField.setAccessible(true);
        ((AtomicBoolean) runningField.get(hashCache)).set(false);
    }
}