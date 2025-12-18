package faang.school.urlshortenerservice.generator;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private ThreadPoolTaskExecutor asyncHashGenerationExecutor;

    @Mock
    private HashEncoder hashEncoder;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(hashCache, "hashCapacity", 5);
        ReflectionTestUtils.setField(hashCache, "hashRefillRatio", 0.2f);

        ReflectionTestUtils.setField(hashCache, "hashPool", new ConcurrentLinkedDeque<String>());
        ReflectionTestUtils.setField(hashCache, "refillInProgress", new AtomicBoolean(false));
    }

    @Test
    void testInitCallsExecutor() {
        hashCache.init();
        verify(asyncHashGenerationExecutor).execute(any(Runnable.class));
    }

    @Test
    void testGetHashFromPool() {
        ConcurrentLinkedDeque<String> pool = new ConcurrentLinkedDeque<>();
        pool.add("preloadedHash");
        ReflectionTestUtils.setField(hashCache, "hashPool", pool);

        String hash = hashCache.getHash();

        assertEquals("preloadedHash", hash);
        assertTrue(pool.isEmpty());
    }

    @Test
    void testGetHashGeneratesNewIfPoolEmpty() {
        when(hashRepository.findNextUnusedId()).thenReturn(42L);
        when(hashEncoder.encodeBase62(42L)).thenReturn("encoded42");

        String hash = hashCache.getHash();

        assertEquals("encoded42", hash);
        verify(hashRepository).findNextUnusedId();
        verify(hashRepository).markUsed(42L);
    }

    @Test
    void testGetHashTriggersRefillWhenLow() {

        ReflectionTestUtils.setField(hashCache, "hashPool", new ConcurrentLinkedDeque<>());
        ReflectionTestUtils.setField(hashCache, "hashCapacity", 10);
        ReflectionTestUtils.setField(hashCache, "hashRefillRatio", 0.5f);

        hashCache.getHash();

        verify(asyncHashGenerationExecutor).execute(any(Runnable.class));
    }
}
