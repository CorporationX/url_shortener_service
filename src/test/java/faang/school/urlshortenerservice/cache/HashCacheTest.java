package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private TaskExecutor hashTaskExecutor;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", 100);
        ReflectionTestUtils.setField(hashCache, "fillPercent", 20);
        ReflectionTestUtils.setField(hashCache, "fillTimeout", 180);
    }

    @Test
    void testGetHash_ShouldReturnHash_WhenQueueIsNotEmpty() {
        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        hashes.add("hash1");
        hashes.add("hash2");

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        assertEquals(1, hashes.size());
        verify(hashTaskExecutor, times(1)).execute(any(Runnable.class));
    }

    @Test
    void testGetHash_ShouldThrowExceptionAndTriggerRefill_WhenQueueIsEmpty() {
        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assertTrue(hashes.isEmpty());

        assertThrows(IllegalStateException.class, () -> hashCache.getHash());
        verify(hashTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void testGetHash_ShouldTriggerAsyncRefill_WhenBelowFillThreshold() {
        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        for (int i = 0; i < 20; i++) {
            hashes.add("hash" + i);
        }

        String result = hashCache.getHash();

        assertEquals("hash0", result);
        verify(hashTaskExecutor, times(1)).execute(any(Runnable.class));
    }

    @Test
    void testRefill_ShouldAddHashesToQueue_WhenNotRefilling() {
        List<String> newHashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.getHashes(100)).thenReturn(newHashes); // Исправлено на 100

        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        AtomicBoolean isRefilling = (AtomicBoolean) ReflectionTestUtils.getField(hashCache, "isRefilling");
        isRefilling.set(false);

        ReflectionTestUtils.invokeMethod(hashCache, "refill");

        assertEquals(3, hashes.size());
        assertFalse(isRefilling.get());
        verify(hashGenerator, times(1)).getHashes(100);
    }

    @Test
    void testRefill_ShouldNotRefill_WhenAlreadyRefilling() {
        AtomicBoolean isRefilling = (AtomicBoolean) ReflectionTestUtils.getField(hashCache, "isRefilling");
        isRefilling.set(true);

        ReflectionTestUtils.invokeMethod(hashCache, "refill");

        verifyNoInteractions(hashGenerator);
    }

}