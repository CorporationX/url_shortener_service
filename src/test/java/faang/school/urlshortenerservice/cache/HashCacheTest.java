package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.cache.CacheProperties;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private ExecutorServiceConfig executorServiceConfig;

    @Mock
    private HashGenerator hashGenerator;

    private static final int COUNT = 3;
    private static final int FILL_PERCENT = 50;
    private Queue<Hash> hashesCache = new ArrayBlockingQueue<>(COUNT);
    private AtomicBoolean filling =  new AtomicBoolean(false);
    private CacheProperties cacheProperties = new CacheProperties();
    private List<Hash> hashes;
    private Hash hashA;
    private Hash hashB;
    private Hash hashC;


    @BeforeEach
    public void init() {
        hashA = new Hash("a");
        hashB = new Hash("b");
        hashC = new Hash("c");
        hashes = Arrays.asList(hashA, hashB, hashC);
        hashesCache.addAll(hashes);
        cacheProperties.setCapacity(COUNT);
        cacheProperties.setFillPercent(FILL_PERCENT);

        ReflectionTestUtils.setField(hashCache, "cacheProperties", cacheProperties);
        ReflectionTestUtils.setField(hashCache, "hashesCache", hashesCache);
        ReflectionTestUtils.setField(hashCache, "filling", filling);
    }

    @Test
    @DisplayName("Success when get hash with full hashesCache")
    public void whenGetHashWithFullHashesCacheThenReturnHash() {
        when(executorServiceConfig.executor()).thenReturn(Executors.newFixedThreadPool(10));
        String result = hashCache.getHash();

        assertNotNull(result);
    }
}