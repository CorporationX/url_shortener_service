package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CacheRefillerAsyncTest {

    @Mock
    private CacheRefillerTransactional transactionalRefiller;

    @InjectMocks
    private CacheRefillerAsync cacheRefillerAsync;

    private List<Long> range;

    @BeforeEach
    void setUp() {
        range = List.of(1L, 2L, 3L);
    }

    @Test
    void refillRedisFromGenerator_ShouldCallTransactionalRefiller() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> cacheRefillerAsync.refillRedisFromGenerator(range));

        future.get();

        verify(transactionalRefiller).refillRedisFromGenerator(range);
    }
}