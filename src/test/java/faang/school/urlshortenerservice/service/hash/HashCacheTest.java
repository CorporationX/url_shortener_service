package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private BlockingQueue<String> cache;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(hashCache, "minCacheFullnessPercent", 20);
        ReflectionTestUtils.setField(hashCache, "capacity", 100);
        ReflectionTestUtils.setField(hashCache, "pool", Executors.newFixedThreadPool(1));
    }

    @Test
    void testGetFreeHash() {
        when(cache.size()).thenReturn(90);
        String expectedHash = "hash";
        when(cache.poll()).thenReturn(expectedHash);

        String actualHash = hashCache.getFreeHash();

        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testGetFreeHashWithGeneration() throws Exception {
        when(cache.size()).thenReturn(10);
        String expectedHash = "hash";
        when(cache.poll()).thenReturn(expectedHash);
        when(cache.remainingCapacity()).thenReturn(90);
        List<String> hashes = IntStream
                .rangeClosed(1, 90)
                .boxed().map(number -> "hash" + number)
                .toList();
        when(hashRepository.getHashWithCustomBatch(90)).thenReturn(hashes);

        String actualHash = hashCache.getFreeHash();

        assertEquals(expectedHash, actualHash);
        Thread.sleep(2000);
        verify(hashGenerator).generateBatch();
        verify(cache).addAll(hashes);
    }
}