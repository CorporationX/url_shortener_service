package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private HashCache hashCache;

    private final int capacity = 5;

    List<String> hashList = List.of("123", "345", "678", "789", "890");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", capacity);
        double thresholdPercent = 20.0;
        ReflectionTestUtils.setField(hashCache, "thresholdPercent", thresholdPercent);
    }

    @Test
    void initSuccessTest() {
        List<String> expectedHashes = hashList;
        when(hashGenerator.getHashList(capacity)).thenReturn(expectedHashes);

        hashCache.init();

        Queue<String> actualHashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assertNotNull(actualHashes);
        assertEquals(expectedHashes.size(), actualHashes.size());
        assertTrue(actualHashes.containsAll(expectedHashes));

        verify(hashGenerator, times(1)).getHashList(capacity);
    }

    @Test
    void getHashSuccessTest() {
        Queue<String> hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashList);
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        String result = hashCache.getHash();

        assertEquals("123", result);
        assertEquals(4, hashes.size());

        verify(hashGenerator, never()).generateHashList();
    }

    @Test
    void getHashBelowThresholdSuccessTest() {

        Queue<String> hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(List.of());
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        AtomicBoolean isRefreshing = new AtomicBoolean(false);
        ReflectionTestUtils.setField(hashCache, "isRefreshing", isRefreshing);

        doAnswer(invocation -> {
            hashes.addAll(hashList);
            return null;
        }).when(executorService).submit(any(Runnable.class));

        String hash = hashCache.getHash();

        assertEquals("123", hash);
        assertTrue(isRefreshing.get());

        verify(executorService).submit(any(Runnable.class));
    }
}