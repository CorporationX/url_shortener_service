package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalCasheServiceTest {

    @Mock
    private HashGeneratorService hashGeneratorService;

    @Mock
    private Queue<String> mockQueue;

    @InjectMocks
    private LocalCasheService localCasheService;
    private final int TEST_CAPACITY = 5;
    private final double TEST_FILL_PERCENT = 0.2d;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(localCasheService, "capacity", TEST_CAPACITY);
        ReflectionTestUtils.setField(localCasheService, "fillPercent", TEST_FILL_PERCENT);
    }

    @Test
    void testInit_WithValidCapacity() {

        List<String> mockHashes = List.of("hash1", "hash2");
        when(hashGeneratorService.getHashes(TEST_CAPACITY)).thenReturn(mockHashes);

        localCasheService.init();

        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(localCasheService, "hashes");
        assertEquals(mockHashes.size(), hashes.size());
        assertTrue(hashes.containsAll(mockHashes));
    }

    @Test
    void testInit_WithInvalidCapacity() {
        ReflectionTestUtils.setField(localCasheService, "capacity", 0);

        assertThrows(IllegalStateException.class, () -> localCasheService.init());
    }

    @Test
    void testGetHash_WhenQueueNeedsRefill_CallsGetHashesAsyncOnce() {

        ReflectionTestUtils.setField(localCasheService, "capacity", 100);
        ReflectionTestUtils.setField(localCasheService, "fillPercent", 0.2f);
        ReflectionTestUtils.setField(localCasheService, "hashes", mockQueue);

        when(mockQueue.poll()).thenReturn("testHash");
        when(mockQueue.size()).thenReturn(19); // 19% < 20% (порог заполнения)

        when(hashGeneratorService.getHashesAsync(eq(100L)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));

        String result = localCasheService.getHash();

        assertEquals("testHash", result);

        verify(mockQueue).poll();

        verify(hashGeneratorService, times(1)).getHashesAsync(eq(100L));
    }

    @Test
    void testGetHash_TriggersRefillWhenNeeded() {
        Queue<String> mockQueue = new java.util.LinkedList<>(List.of("h1"));
        ReflectionTestUtils.setField(localCasheService, "hashes", mockQueue);
        ReflectionTestUtils.setField(localCasheService, "filling", new AtomicBoolean(false));

        when(hashGeneratorService.getHashesAsync(TEST_CAPACITY))
                .thenReturn(CompletableFuture.completedFuture(List.of("h2", "h3")));

        localCasheService.getHash();

        verify(hashGeneratorService).getHashesAsync(TEST_CAPACITY);
        assertEquals(2, mockQueue.size());
    }

    @Test
    void testShouldRefillHashes_WhenBelowThreshold() {
        Queue<String> mockQueue = new java.util.LinkedList<>(List.of("h1"));
        ReflectionTestUtils.setField(localCasheService, "hashes", mockQueue);

        assertTrue(localCasheService.shouldRefillHashes());
    }

}