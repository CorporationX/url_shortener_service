package faang.school.urlshortenerservice.local_cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class LocalCacheTest {
    @InjectMocks
    private LocalCache localCache;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private Queue<Hash> queue;
    @Mock
    private AtomicBoolean aBoolean;

    private long getSize = 2700;
    private long minSize = 2500;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(localCache, "getSize", getSize);
        ReflectionTestUtils.setField(localCache, "minSize", minSize);
    }

    @Test
    void testGetCache_whenCacheIsEmpty_thenShouldAddNewHash() {
        when(queue.poll()).thenReturn(new Hash("hash1"));
        when(queue.size()).thenReturn(7000);

        String result = localCache.getCache();

        assertEquals("hash1", result);

        verify(aBoolean,times(0)).compareAndExchange(false,true);
    }

    @Test
    void testAddNewCache(){
        localCache.addNewHash();
        verify(queue).addAll(any());
    }
}