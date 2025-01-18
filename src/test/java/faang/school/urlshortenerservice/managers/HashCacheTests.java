package faang.school.urlshortenerservice.managers;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTests {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(hashCache, "capacity", 10);
        setPrivateField(hashCache, "refillThreshold", 0.5);
        setPrivateField(hashCache, "hasheQueue", new ArrayBlockingQueue<>(10));
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    public void testInit() throws NoSuchFieldException, IllegalAccessException {
        List<Hash> hashList = List.of(new Hash("hash1"), new Hash("hash2"));
        when(hashGenerator.getHashBatchSync()).thenReturn(hashList);

        hashCache.init();

        verify(hashGenerator, times(1)).getHashBatchSync();

        Queue<String> hasheQueue = (Queue<String>) getPrivateField(hashCache, "hasheQueue");
        assertEquals(2, hasheQueue.size());
    }

    private Object getPrivateField(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    @Test
    public void testGetHash() throws NoSuchFieldException, IllegalAccessException {
        Queue<String> hasheQueue = (Queue<String>) getPrivateField(hashCache, "hasheQueue");
        hasheQueue.add("hash1");

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
    }

    @Test
    public void testGetHashRefill() throws NoSuchFieldException, IllegalAccessException {
        Queue<String> hasheQueue = (Queue<String>) getPrivateField(hashCache, "hasheQueue");
        hasheQueue.add("hash1");

        when(hashGenerator.getHashBatch()).thenReturn(CompletableFuture.completedFuture(List.of(new Hash("hash2"))));
        hashCache.getHash();

        verify(hashGenerator, times(1)).getHashBatch();
    }

    @Test
    public void testRefillHashCache() throws NoSuchFieldException, IllegalAccessException {
        List<Hash> hashList = List.of(new Hash("hash1"), new Hash("hash2"));
        CompletableFuture<List<Hash>> futureHashes = CompletableFuture.completedFuture(hashList);

        when(hashGenerator.getHashBatch()).thenReturn(futureHashes);
        hashCache.refillHashCache();

        verify(hashGenerator, times(1)).getHashBatch();
        verify(executorService, times(1)).submit(any(Runnable.class));

        Queue<String> hasheQueue = (Queue<String>) getPrivateField(hashCache, "hasheQueue");
        assertEquals(2, hasheQueue.size());
    }

    @Test
    public void testRefillHashCacheLockFailed() throws NoSuchFieldException, IllegalAccessException {
        ReentrantLock lock = mock(ReentrantLock.class);
        when(lock.tryLock()).thenReturn(false);
        setPrivateField(hashCache, "lock", lock);

        hashCache.refillHashCache();

        verify(hashGenerator, times(0)).getHashBatch();
        verify(executorService, times(0)).submit(any(Runnable.class));
    }
}

