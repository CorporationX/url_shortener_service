package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.service.HashCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarmupTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashCacheService hashCacheService;

    @InjectMocks
    private Warmup warmup;

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Test
    void testWarmUpRun() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> hashGeneratorFuture = new CompletableFuture<>();
        CompletableFuture<Void> hashCacheServiceFuture = new CompletableFuture<>();

        when(hashGenerator.asyncHashRepositoryRefill()).thenReturn(hashGeneratorFuture);
        when(hashCacheService.asyncCacheRefill()).thenReturn(hashCacheServiceFuture);

        Future<Void> future = executorService.submit(() -> warmup.run(), null);
        hashGeneratorFuture.complete(null);
        hashCacheServiceFuture.complete(null);
        future.get();

        verify(hashGenerator, times(1)).asyncHashRepositoryRefill();
        verify(hashCacheService, times(1)).asyncCacheRefill();
    }
}