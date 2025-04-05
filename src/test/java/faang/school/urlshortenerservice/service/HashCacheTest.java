package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @Mock
    private HashRepository hashRepository;

    @Mock
    private ExecutorService executorService;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(hashRepository, executorService, hashGenerator, jdbcTemplate);

        ReflectionTestUtils.setField(hashCache, "maxCacheSize", 100);
        ReflectionTestUtils.setField(hashCache, "thresholdPercent", 20);
        ReflectionTestUtils.setField(hashCache, "batchSize", 50);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executorService).submit(any(Runnable.class));
    }

    @Test
    void getHash_whenCacheBelowThreshold_shouldRefillCache() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.add("hash1");
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);
        ReflectionTestUtils.setField(hashCache, "isRefilling", new AtomicBoolean(false));

        when(hashRepository.getUniqueNumbers(anyInt())).thenReturn(Arrays.asList(10L, 20L, 30L));

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        verify(executorService).submit(any(Runnable.class));
        verify(hashRepository).getUniqueNumbers(50);
    }

    @Test
    void getHashCache_shouldReturnMultipleHashes() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.add("hash1");
        queue.add("hash2");
        queue.add("hash3");
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        String first = hashCache.getNextHash();
        String second = hashCache.getNextHash();
        String third = hashCache.getNextHash();

        assertEquals("hash1", first);
        assertEquals("hash2", second);
        assertEquals("hash3", third);
    }
}
