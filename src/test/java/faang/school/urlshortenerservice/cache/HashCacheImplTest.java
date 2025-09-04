package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class HashCacheImplTest {
    private static final int CACHE_CAPACITY = 10;
    private static final int MIN_LIMIT_PERCENT = 20;

    @InjectMocks
    private HashCacheImpl hashCache;

    @Mock
    private Executor executor;
    @Mock
    private HashGenerator generator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private AtomicBoolean isFilling;
    @Mock
    private BlockingQueue<String> cache;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(hashCache, "cacheCapacity", CACHE_CAPACITY);
        ReflectionTestUtils.setField(hashCache, "minLimitPercent", MIN_LIMIT_PERCENT);
    }

    @Test
    public void testGetHashAboveCapacity() {
        initSerialExecutor();

        int size = 5;
        when(cache.size()).thenReturn(size);

        hashCache.getHash();

        verify(cache, times(1)).poll();
        verify(hashRepository, never()).getHashBatch(anyInt());
        verify(cache, never()).addAll(anyList());
        verify(generator, never()).generateBatch();
    }

    @Test
    public void testGetHashBelowCapacityAndIsFilling() {
        initSerialExecutor();
        ReflectionTestUtils.setField(hashCache, "isFilling", new AtomicBoolean(true));

        int size = 1;
        when(cache.size()).thenReturn(size);

        hashCache.getHash();

        verify(cache, times(1)).poll();
        verify(hashRepository, never()).getHashBatch(anyInt());
        verify(cache, never()).addAll(anyList());
        verify(generator, never()).generateBatch();
    }

    @Test
    public void testGetHashBelowCapacityAndIsNotFillingAndFullBatch() {
        initSerialExecutor();

        int size = 1;
        List<String> hashes = IntStream.range(0, CACHE_CAPACITY)
                .mapToObj(i -> "hash-" + i)
                .toList();
        when(cache.size()).thenReturn(size);
        when(hashRepository.getHashBatch(CACHE_CAPACITY - size)).thenReturn(hashes);

        hashCache.getHash();

        verify(cache, times(1)).poll();
        verify(hashRepository, times(1)).getHashBatch(anyInt());
        verify(cache, times(1)).addAll(anyList());
        verify(generator, never()).generateBatch();
    }

    @Test
    public void testGetHashBelowCapacityAndIsNotFillingAndNotFullBatch() {
        initSerialExecutor();

        int size = 1;
        List<String> hashes = IntStream.range(0, CACHE_CAPACITY - 2)
                .mapToObj(i -> "hash-" + i)
                .toList();
        when(cache.size()).thenReturn(size);
        when(hashRepository.getHashBatch(CACHE_CAPACITY - size)).thenReturn(hashes);

        hashCache.getHash();

        verify(cache, times(1)).poll();
        verify(hashRepository, times(1)).getHashBatch(anyInt());
        verify(cache, times(1)).addAll(anyList());
        verify(generator, times(1)).generateBatch();
    }

    private void initSerialExecutor() {
        Executor sameThreadExecutor = Runnable::run;
        ReflectionTestUtils.setField(hashCache, "executor", sameThreadExecutor);
    }
}
