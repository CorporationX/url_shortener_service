package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    private HashCache hashCache;
    private HashJpaRepository hashRepository;
    private ExecutorService executorService;
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        hashRepository = mock(HashJpaRepository.class);
        hashGenerator = mock(HashGenerator.class);
        executorService = mock(ExecutorService.class);
        hashCache = new HashCache(hashGenerator, executorService, hashRepository);
        hashCache.setCache(new ArrayBlockingQueue<>(10));
    }

    @Test
    void testRefreshHasahes() throws Exception {
        List<String> mockHashes = Arrays.asList("hash1", "hash2", "hash3");
        when(hashRepository.findAndDelete(anyLong())).thenReturn(mockHashes);

        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            new Thread(() -> {
                task.run();
                latch.countDown();
            }).start();
            return null;
        }).when(executorService).submit(any(Runnable.class));
        hashCache.refreshCache();
        latch.await();

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(hashRepository).findAndDelete(captor.capture());
    }
}