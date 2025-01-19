package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.properties.HashCacheProperties;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private TaskExecutor taskExecutor;

    @InjectMocks
    private HashCache hashCache;

    private HashCacheProperties props;

    @BeforeEach
    void setUp() {
        props = new HashCacheProperties();
        props.setMaxSize(10);
        props.setRefillThresholdPercent(20);
        HashCacheProperties.ThreadPoolProperties tp = new HashCacheProperties.ThreadPoolProperties();
        tp.setCoreSize(1);
        tp.setMaxSize(1);
        tp.setQueueCapacity(1);
        props.setThreadPool(tp);

        ReflectionTestUtils.setField(hashCache, "properties", props);

        hashCache.init();
    }

    @Test
    void testGetHashAboveThreshold() {
        LinkedBlockingQueue<String> internalQueue =
                (LinkedBlockingQueue<String>) ReflectionTestUtils.getField(hashCache, "cache");

        assertNotNull(internalQueue);
        internalQueue.offer("hash1");
        internalQueue.offer("hash2");
        internalQueue.offer("hash3");
        internalQueue.offer("hash4");
        internalQueue.offer("hash5");

        String got = hashCache.getHash();
        assertEquals("hash1", got);

        verifyNoInteractions(hashRepository);
        verifyNoInteractions(hashGenerator);
        verify(taskExecutor, never()).execute(any(Runnable.class));
    }

    @Test
    void testGetHashBelowThreshold() {
        LinkedBlockingQueue<String> internalQueue =
                (LinkedBlockingQueue<String>) ReflectionTestUtils.getField(hashCache, "cache");
        assertNotNull(internalQueue);
        internalQueue.offer("hashA");

        String got = hashCache.getHash();
        assertEquals("hashA", got);

        verify(taskExecutor, times(1)).execute(any(Runnable.class));

        verifyNoInteractions(hashRepository);
        verifyNoInteractions(hashGenerator);
    }

    @Test
    void testRefillCacheLogic() {
        when(hashRepository.getHashBatch(anyInt())).thenReturn(Arrays.asList("db1", "db2", "db3"));

        ReflectionTestUtils.invokeMethod(hashCache, "refillCache");

        verify(hashRepository, times(1)).getHashBatch( anyInt() );

        verify(hashGenerator, times(1)).generateBatch();

        LinkedBlockingQueue<String> internalQueue =
                (LinkedBlockingQueue<String>) ReflectionTestUtils.getField(hashCache, "cache");
        assertNotNull(internalQueue);
        assertEquals(3, internalQueue.size());
    }
}

