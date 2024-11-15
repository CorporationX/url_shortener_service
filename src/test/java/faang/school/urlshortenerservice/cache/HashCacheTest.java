package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.executor.ExecutorServiceConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private ExecutorServiceConfig executorServiceConfig;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private Queue<Hash> hashesCache;

    @Mock
    private AtomicBoolean filling;

    private static final int COUNT = 3;
    private static final int FILL_PERCENT = 50;
//    private Queue<Hash> hashesCache;
//    private AtomicBoolean filling;
    private List<Hash> hashes;
    private Hash hashA;
    private Hash hashB;
    private Hash hashC;


    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(hashCache, "capacity", COUNT);
        ReflectionTestUtils.setField(hashCache, "fillPercent", FILL_PERCENT);
        ReflectionTestUtils.setField(hashesCache, "hashesCache", new ArrayBlockingQueue<>(COUNT));
        ReflectionTestUtils.setField(filling, "filling", false);

//        hashesCache = new ArrayBlockingQueue<>(COUNT);
//        filling = new AtomicBoolean(false);
        hashA = new Hash("a");
        hashB = new Hash("b");
        hashC = new Hash("c");
        hashes = Arrays.asList(hashA, hashB, hashC);
    }

    @Test
    @DisplayName("Success when init hashesCache")
    public void whenInitHashesCacheThenSuccess() {
//        when(hashGenerator.getHashesForCache(COUNT)).thenReturn(hashes);
//
//        hashCache.initHashesCache();
//
//        assertTrue(hashesCache.contains(hashA));
//        assertTrue(hashesCache.contains(hashB));
//        assertTrue(hashesCache.contains(hashC));
//        verify(hashGenerator).getHashesForCache(COUNT);
    }
}