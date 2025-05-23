package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.hash.HashCacheConfig;
import faang.school.urlshortenerservice.exception.HashUnavailableException;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест HashCache")
public class HashCacheTest {
    @Mock
    private HashCacheConfig hashCacheConfig;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private ExecutorService hashRefillExecutor;

    @Mock
    private BlockingQueue<String> availableHashes;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        when(hashCacheConfig.getMaxSize()).thenReturn(100);
        when(hashCacheConfig.getRefillThresholdPercent()).thenReturn(20);

        hashCache.init();
    }

    @Nested
    @DisplayName("Получение хэша")
    class GetHashTests {

        @Test
        @DisplayName("Получение хэша, когда кэш полный")
        void givenFullCache_whenGetHash_thenReturnsFromCache() throws Exception {
            when(availableHashes.size()).thenReturn(100);
            when(availableHashes.take()).thenReturn("test-hash");

            String actualHash = hashCache.getHash();

            assertEquals("test-hash", actualHash);
        }

        @Test
        @DisplayName("Когда очередь пуста, тогда бросает HashUnavailableException")
        void givenEmptyQueue_whenGetHash_thenThrowsException() throws Exception {
            when(availableHashes.take()).thenThrow(new InterruptedException());

            assertThrows(HashUnavailableException.class, () -> hashCache.getHash());
        }
    }

    @Nested
    @DisplayName("Тесты пополнения кэша")
    class RefillCacheTests {

        @Test
        @DisplayName("Когда количество ниже порога, тогда инициирует пополнение")
        void givenLowQueueSize_whenGetHash_thenTriggersRefill() throws Exception {
            when(availableHashes.size()).thenReturn(15);

            hashCache.getHash();

            verify(hashRefillExecutor).submit(any(Runnable.class));
        }
    }
}
