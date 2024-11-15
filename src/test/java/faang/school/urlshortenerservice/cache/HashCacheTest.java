package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {


    @Mock
    private Executor executorForHashCache;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashCacheProperty cacheProperty;

    @Mock
    private HashGenerator generator;

    @InjectMocks
    private HashCacheImpl hashCache;

    private ConcurrentLinkedQueue<String> hashQueue;

    @BeforeEach
    public void setUp() {
        hashQueue = new ConcurrentLinkedQueue<>(List.of("hash1", "hash2"));
        ReflectionTestUtils.setField(hashCache, "hashQueue", hashQueue);

        ReflectionTestUtils.setField(hashCache, "threshold", 20);
    }

    @Test
    public void testGetHashTriggersRefillWhenBelowThreshold() throws Exception {
        when(cacheProperty.getMaxQueueSize()).thenReturn(100);
        when(hashRepository.findTopNHashes(anyInt())).thenReturn(List.of("hash3", "hash4"));

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(executorForHashCache).execute(any());

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(hashRepository, times(1)).findTopNHashes(argumentCaptor.capture());

        int actualArgument = argumentCaptor.getValue() - 1;
        int expectedArgument = 100 - hashQueue.size();

        assertEquals(expectedArgument, actualArgument);
    }

    @Test
    public void testCacheRefillDirectly() throws Exception {
        ReflectionTestUtils.setField(hashCache, "hashQueue", new ConcurrentLinkedQueue<>());

        List<String> hashes = List.of("newHash1", "newHash2");
        when(hashRepository.findTopNHashes(anyInt())).thenReturn(hashes);

        Method refillingCacheWithHashesMethod = HashCacheImpl.class.getDeclaredMethod("refillingCacheWithHashes", int.class);
        refillingCacheWithHashesMethod.setAccessible(true);

        refillingCacheWithHashesMethod.invoke(hashCache, 2);

        assertEquals(2, hashCache.getHashQueue().size());
        assertTrue(hashCache.getHashQueue().contains("newHash1"));
        assertTrue(hashCache.getHashQueue().contains("newHash2"));
    }
}
