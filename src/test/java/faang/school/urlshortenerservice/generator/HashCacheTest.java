package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setup() {
        hashCache = new HashCache(hashRepository);
        ReflectionTestUtils.setField(hashCache, "hashCapacity", 10);
        ReflectionTestUtils.setField(hashCache, "hashRefillRatio", 0.2f);
    }

    private ConcurrentLinkedQueue<String> pool() {
        return (ConcurrentLinkedQueue<String>) ReflectionTestUtils.getField(hashCache, "hashPool");
    }

    private AtomicBoolean refillFlag() {
        return (AtomicBoolean) ReflectionTestUtils.getField(hashCache, "refillInProgress");
    }

    @Test
    void testInitRefillCacheSuccess() {
        when(hashRepository.getFreeIds(10)).thenReturn(List.of(1L, 2L, 3L));
        hashCache.init();
        assertEquals(3, pool().size());
    }

    @Test
    void testGetHashReturnsFromPoolWhenAvailable() {
        pool().add("b");
        assertEquals("b", hashCache.getHash());
        verify(hashRepository, never()).findNextUnusedId();
    }

    @Test
    void testGetHashGeneratesHashWhenPoolEmpty() {
        when(hashRepository.findNextUnusedId()).thenReturn(100L);
        String hash = hashCache.getHash();

        assertEquals("bM", hash);
        verify(hashRepository).markUsed(100L);
    }

    @Test
    void testGetHashTriggersRefillWhenBelowThreshold() {
        pool().add("x");
        when(hashRepository.getFreeIds(10)).thenReturn(List.of(3L, 4L));
        String hash = hashCache.getHash();

        assertTrue(pool().contains("d"));
        assertTrue(pool().contains("e"));
        assertEquals("x", hash);
    }

    @Test
    void testRefillCacheAddsEncodedValues() {
        when(hashRepository.getFreeIds(10)).thenReturn(List.of(1L, 62L, 63L));
        hashCache.refillCache();

        ConcurrentLinkedQueue<String> pool = pool();
        assertTrue(pool.contains("b"));
        assertTrue(pool.contains("ba"));
        assertTrue(pool.contains("bb"));
    }

    @Test
    void testGetHashDoesNotTriggerRefillWhenAlreadyInProgress() {
        pool().add("x");
        refillFlag().set(true);
        String hash = hashCache.getHash();

        assertEquals("x", hash);
        verify(hashRepository, never()).getFreeIds(anyInt());
    }
}