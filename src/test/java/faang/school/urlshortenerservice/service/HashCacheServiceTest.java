package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheServiceTest {

    @Mock
    private ConcurrentLinkedQueue<String> hashCache;

    @Mock
    private ThreadPoolTaskExecutor shortenerTaskExecutor;

    @Mock
    private HashService hashService;

    @Mock
    private HashGenerator hashGenerator;

    @Spy
    @InjectMocks
    private HashCacheService hashCacheService;

    private Long cacheCapacity;
    private double lowThresholdRate;
    private String hash;
    public List<String> hashes;

    @BeforeEach
    void setUp() {
        cacheCapacity = 100L;
        lowThresholdRate = 0.2;
        hash = "HASHHH";
        ReflectionTestUtils.setField(hashCacheService, "cacheCapacity", cacheCapacity);
        ReflectionTestUtils.setField(hashCacheService, "lowThresholdRate", lowThresholdRate);
        hashes = new ArrayList<>(List.of("hash1", "hash2", "hash3"));
    }

    @Test
    void testGetHashWithoutCacheRefillCall() {
        when(hashCache.size()).thenReturn(100);
        when(hashCache.poll()).thenReturn(hash);

        String result = hashCacheService.getHash();

        verify(hashCache, times(1)).size();
        verify(hashCache, times(1)).poll();
        verify(hashCache, never()).addAll(any());
        verifyNoInteractions(hashGenerator);
        assertThat(result).isEqualTo(hash);
    }

    @Test
    void testGetHashWithCacheRefillCall() {
        when(hashCache.size()).thenReturn(10);
        when(hashCache.poll()).thenReturn(hash);

        String result = hashCacheService.getHash();

        verify(hashCache, times(1)).size();
        verify(hashCache, times(1)).poll();
        verify(shortenerTaskExecutor, times(1)).execute(any(Runnable.class));
        assertThat(result).isEqualTo(hash);
    }

    @Test
    void testAsyncCacheRefillSuccess_RefillNotInProgress() {
        prepareExecutorAndHashCacheService();

        when(hashService.getAndDeleteHashBatch(cacheCapacity)).thenReturn(hashes);

        hashCacheService.asyncCacheRefill().join();

        verify(hashService, times(1)).getAndDeleteHashBatch(cacheCapacity);
        verify(hashCache, times(1)).addAll(hashes);
        verify(hashGenerator, times(1)).asyncHashRepositoryRefill();
    }

    @Test
    void testAsyncCacheRefillDoesNothing_RefillInProgress() {
        prepareExecutorAndHashCacheService();
        ReflectionTestUtils.setField(hashCacheService, "isRefilling", new AtomicBoolean(true));

        hashCacheService.asyncCacheRefill();

        verifyNoInteractions(hashService);
        verifyNoInteractions(hashCache);
        verifyNoInteractions(hashGenerator);
    }

    private void prepareExecutorAndHashCacheService() {
        shortenerTaskExecutor = new ThreadPoolTaskExecutor();
        shortenerTaskExecutor.initialize();
        hashCacheService = new HashCacheService(hashCache, shortenerTaskExecutor, hashService, hashGenerator);
        ReflectionTestUtils.setField(hashCacheService, "cacheCapacity", cacheCapacity);
        ReflectionTestUtils.setField(hashCacheService, "lowThresholdRate", lowThresholdRate);
    }
}