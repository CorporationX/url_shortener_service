package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.config.async.ThreadPool;
import faang.school.urlshortenerservice.properties.HashCacheQueueProperties;
import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import faang.school.urlshortenerservice.util.Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashGeneratorTest {

    @Mock
    private HashCacheQueueProperties properties;

    @Mock
    private HashRepositoryImpl hashRepository;

    @Spy
    private Base62Encoder base62Encoder;

    @Mock
    private ThreadPool threadPool;

    @Spy
    private Util util;

    @InjectMocks
    private HashGenerator hashGenerator;

    ThreadPoolTaskExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("async-hash-fill-exec-test");
        executor.initialize();
    }

    @AfterEach
    void tearDown() {
        ThreadPoolTaskExecutor executor = threadPool.hashGeneratorExecutor();
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    public void generateHashTest() {
        int batchSize = 50;
        int fillingBatchQuantity = 10;

        List<Long> uniqueElements = new ArrayList<>();
        for (long i = 1; i <= batchSize; i++) {
            uniqueElements.add(i);
        }

        when(hashRepository.getUniqueNumbers(batchSize)).thenReturn(uniqueElements);
        when(properties.getFillingBatchesQuantity()).thenReturn(fillingBatchQuantity);
        when(threadPool.hashGeneratorExecutor()).thenReturn(executor);

        CompletableFuture<Void> result = hashGenerator.generateBatchHashes(batchSize);

        assertTrue(result.isDone());

        verify(hashRepository).saveHashes(anyList());
        verify(base62Encoder, times(fillingBatchQuantity)).encode(anyList());
        verify(util, times(1)).getBatches(uniqueElements, fillingBatchQuantity);
    }
}