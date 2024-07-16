package faang.school.urlshortenerservice.HashCache;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    private HashCache hashCache;
    private List<String> hashes;
    private List<String> addedHashes;
    private int capacity;


    @BeforeEach
    public void _init_(){
        capacity = 3;
        int minPercentageToFill = 10;
        hashes = List.of("testHash1", "testHash2", "testHash3");
        addedHashes = List.of("testHash4", "testHash5", "testHash6");
        hashCache = new HashCache(capacity, minPercentageToFill, hashGenerator);
        when(hashGenerator.getHashes(capacity)).thenReturn(hashes);
        hashCache.init();
    }

    @Test
    public void testGetHashWhenQueueContainsEnoughHashes(){
        String hash = hashCache.getHash();

        assertNotNull(hash);
        assertTrue(hashes.contains(hash));
    }

    @Test
    public void testGetHashHasNotEnoughHashes(){
        hashCache.getHash();
        hashCache.getHash();
        hashCache.getHash();

        when(hashGenerator.getHashesAsync(capacity))
                .thenReturn(CompletableFuture.completedFuture(addedHashes));

        String result = hashCache.getHash();

        InOrder inOrder = inOrder(hashGenerator);
        inOrder.verify(hashGenerator, times(1)).getHashesAsync(capacity);
        assertNotNull(result);
        assertTrue(addedHashes.contains(result));
    }
}
