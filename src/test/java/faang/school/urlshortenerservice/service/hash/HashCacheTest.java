package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    private HashCache hashCache;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private TaskExecutor threadPool;

    private ArrayBlockingQueue<String> cache;

    @BeforeEach
    public void setUp() {
        int cacheSize = 200;
        cache = new ArrayBlockingQueue<>(cacheSize);

        hashCache = new HashCache(hashGenerator, hashRepository, threadPool, cache);
    }

    @Test
    public void testFillCacheOnBoot() {
        // arrange
        List<String> hashes = List.of("000001", "000002", "00zwer", "000512");
        when(hashGenerator.generateBatch()).thenReturn(CompletableFuture.completedFuture(null));
        when(hashRepository.getHashBatch(0)).thenReturn(hashes);

        // act
        hashCache.fillCacheOnBoot();

        // assert
        List<String> storedHashes = new ArrayList<>();
        while (!cache.isEmpty()) {
            storedHashes.add(cache.poll());
        }
        assertEquals(hashes, storedHashes);
    }
}
