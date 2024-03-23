package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    private final Queue<String> cache = new ArrayBlockingQueue<>(1000);
    @Mock
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    @InjectMocks
    private HashCache hashCache;

    @Test
    void getHash() throws InterruptedException {
        hashCache = new HashCache(hashGenerator, hashRepository, executorService);

        List<String> hashes = List.of("hash1", "hash2", "hash3");
        Mockito.when(hashRepository.getHashBatch()).thenReturn(hashes);
        ReflectionTestUtils.setField(hashCache, "cache", cache);
        ReflectionTestUtils.setField(hashCache, "cacheSize", 1000);
        ReflectionTestUtils.setField(hashCache, "fillPercent", 20.0);

        hashCache.getHash();

        Mockito.verify(hashRepository, Mockito.times(1)).getHashBatch();
        Mockito.verify(hashGenerator, Mockito.times(1)).generateBatch();
    }
}