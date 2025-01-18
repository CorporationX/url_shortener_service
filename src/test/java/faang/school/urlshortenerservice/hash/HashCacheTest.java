package faang.school.urlshortenerservice.hash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    public static final int CAPACITY = 5;

    @Mock
    private HashGenerator hashGenerator;
    private HashCache hashCache;

    @BeforeEach
    public void setUp() {
        hashCache = new HashCache(hashGenerator,
                Executors.newSingleThreadExecutor(),
                Executors.newSingleThreadExecutor());

        ReflectionTestUtils.setField(hashCache, "capacity", CAPACITY);
        ReflectionTestUtils.setField(hashCache, "localCache", new ArrayBlockingQueue<>(CAPACITY));
        ReflectionTestUtils.setField(hashCache, "isCacheRefreshing", new AtomicBoolean(false));
    }

    @Test
    public void testInit() {
        when(hashGenerator.getHashBatch(CAPACITY)).thenReturn(new ArrayList<>());

        hashCache.init();

        verify(hashGenerator).generateBatch();
        verify(hashGenerator).getHashBatch(CAPACITY);
    }

    @Test
    public void testGetHash() {
        when(hashGenerator.isHashCountBelowThreshold(anyInt())).thenReturn(true);
        when(hashGenerator.tryLock()).thenReturn(true);

        hashCache.getHash();

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(hashGenerator).tryLock();
            verify(hashGenerator).generateBatch();
            verify(hashGenerator).unlock();
        });
    }
}