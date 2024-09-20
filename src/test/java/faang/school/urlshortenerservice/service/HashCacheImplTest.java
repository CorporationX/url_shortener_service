package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class HashCacheImplTest {

    private final int cacheCapacity = 1000;
    private final int fillPercent = 20;
    private final ExecutorService mockedExecutor = mock(ExecutorService.class);
    private final HashGenerator mockedHashGenerator = mock(HashGenerator.class);

    private final HashCacheImpl hashCache
            = new HashCacheImpl(cacheCapacity, fillPercent, mockedExecutor, mockedHashGenerator);

    @BeforeEach
    void setUp() {
    }

    @Test
    void getHash_capacityGreaterThanThreshold_justPoll() {
        int testCapacity = 2;
        LinkedBlockingQueue<String> testCache = new LinkedBlockingQueue<>(testCapacity);
        testCache.addAll(List.of("1", "2"));
        ReflectionTestUtils.setField(hashCache, "cacheCapacity", testCapacity);
        ReflectionTestUtils.setField(hashCache, "cache", testCache);

        String result = hashCache.getHash();

        assertEquals("1", result);
        assertEquals(1, testCache.size());
        verifyNoInteractions(mockedExecutor, mockedHashGenerator);
    }

    @Test
    void getHash_capacityLessThanThreshold_startAsyncRefillAndPoll() {
        LinkedBlockingQueue<String> testCache = new LinkedBlockingQueue<>(cacheCapacity);
        List<String> initialHashes = List.of("1", "2");
        testCache.addAll(initialHashes);
        ReflectionTestUtils.setField(hashCache, "cache", testCache);

        doAnswer(invocation -> { // Вызываем метод, который должен быть вызван асинхронно
                    invocation.getArgument(0, Runnable.class).run();
                    return null;
                })
                .when(mockedExecutor).execute(any(Runnable.class));

        List<String> newHashes = List.of("3", "4");
        int remainingCapacity = testCache.remainingCapacity();
        when(mockedHashGenerator.getHashes(remainingCapacity))
                .thenReturn(newHashes);


        String result = hashCache.getHash();

        assertEquals("1", result);
        assertEquals(3, testCache.size());
        assertTrue(testCache.contains("2"));
        assertTrue(testCache.containsAll(newHashes));
        verify(mockedExecutor, times(1)).execute(any(Runnable.class));
        verify(mockedHashGenerator, times(1)).getHashes(remainingCapacity);
        verify(mockedHashGenerator, times(1)).generateBatchIfNeededAsync();
    }
}