package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private AsyncHashService asyncHashService;
    @InjectMocks
    private HashCache hashCache;

    @Test
    void getHashSuccessTest() {
        AtomicBoolean filling = new AtomicBoolean(false);
        Queue<String> hashes = new ArrayBlockingQueue<>(1000);
        ReflectionTestUtils.setField(hashCache, "capacity", 1000);
        ReflectionTestUtils.setField(hashCache, "fillPercent", 20);
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);
        ReflectionTestUtils.setField(hashCache, "filling", filling);
        when(asyncHashService.getHashesAsync(1000)).thenReturn(CompletableFuture.completedFuture(List.of("hash1", "hash2")));
        assertDoesNotThrow(() -> {
            String hash = hashCache.getHash();
            assertEquals("hash1", hash);
        });
        verify(asyncHashService).getHashesAsync(1000);
    }

    @Test
    void getHashWithoutFillingHashesByCapacityConditionSuccessTest() {
        AtomicBoolean filling = new AtomicBoolean(false);
        Queue<String> hashes = new ArrayBlockingQueue<>(100);
        hashes.addAll(List.of("hash1", "hash2"));
        ReflectionTestUtils.setField(hashCache, "capacity", 100);
        ReflectionTestUtils.setField(hashCache, "fillPercent", 1);
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);
        ReflectionTestUtils.setField(hashCache, "filling", filling);
        assertDoesNotThrow(() -> {
            String hash = hashCache.getHash();
            assertEquals("hash1", hash);
        });
        verify(asyncHashService, never()).getHashesAsync(100);
    }

    @Test
    void getHashWithoutFillingHashesByFillingFlagConditionSuccessTest() {
        AtomicBoolean filling = new AtomicBoolean(true);
        Queue<String> hashes = new ArrayBlockingQueue<>(1000);
        hashes.addAll(List.of("hash1", "hash2"));
        ReflectionTestUtils.setField(hashCache, "capacity", 1000);
        ReflectionTestUtils.setField(hashCache, "fillPercent", 20);
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);
        ReflectionTestUtils.setField(hashCache, "filling", filling);
        assertDoesNotThrow(() -> {
            String hash = hashCache.getHash();
            assertEquals("hash1", hash);
        });
        verify(asyncHashService, never()).getHashesAsync(1000);
    }
}
