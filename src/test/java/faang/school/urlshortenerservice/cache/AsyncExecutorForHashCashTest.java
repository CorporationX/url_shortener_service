package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.cache.AsyncExecutorForHashCash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AsyncExecutorForHashCashTest {

    @Mock
    HashService hashService;
    @Mock
    HashGenerator hashGenerator;
    @InjectMocks
    AsyncExecutorForHashCash asyncExecutorForHashCash;

    ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    @Test
    void testExclusiveTransferHashBatchSuccessful() {
        int testBatchSize = 10;

        asyncExecutorForHashCash.exclusiveTransferHashBatch(testBatchSize, queue);

        verify(hashService, times(1)).getHashBatch(anyInt());
    }

    @Test
    void testAsyncGenerateBatchSuccessful() {

        asyncExecutorForHashCash.asyncGenerateBatch();

        verify(hashGenerator, times(1)).generateBatch();
    }
}
