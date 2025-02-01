package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        hashCache = new HashCache(hashGenerator);
        ReflectionTestUtils.setField(hashCache, "capacity", 1000);
        ReflectionTestUtils.setField(hashCache, "percent", 20.0);
        hashCache.init();
    }

    @Test
    public void testInitSuccess() {
        Assertions.assertNotNull(ReflectionTestUtils.getField(hashCache, "hashes"));
        verify(hashGenerator, Mockito.times(1)).getHashes(1000);
    }

    @Test
    public void testGetHashBelowFillPercent() {
        when(hashGenerator.getHashesAsync(anyLong())).thenReturn(CompletableFuture.completedFuture(List.of(new Hash("asyncHash"))));
        Hash hash = hashCache.getHash();
        Assertions.assertNotNull(hash);
        verify(hashGenerator, Mockito.times(1)).getHashesAsync(anyLong());
    }

    @Test
    public void testGetHashAboveFillPercent() {
        ArrayBlockingQueue<Hash> queue = new ArrayBlockingQueue<>(1000);
        for (int i = 0; i < 900; i++) {
            queue.add(new Hash("hash" + i));
        }
        ReflectionTestUtils.setField(hashCache, "hashes", queue);
        Hash hash = hashCache.getHash();
        Assertions.assertNotNull(hash);
        verify(hashGenerator, never()).getHashesAsync(anyLong());
    }
}