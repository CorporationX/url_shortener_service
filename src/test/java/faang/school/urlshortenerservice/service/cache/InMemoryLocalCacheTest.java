package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.HashDao;
import faang.school.urlshortenerservice.schedule.HashDbMaintainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryLocalCacheTest {
    private final Executor taskExecutor = Runnable::run;

    @Mock
    private HashDao hashDao;

    @Mock
    private HashDbMaintainer hashDbMaintainer;

    private InMemoryLocalCache inMemoryLocalCache;

    @BeforeEach
    void setUp() {
        inMemoryLocalCache = new InMemoryLocalCache(hashDao, taskExecutor, hashDbMaintainer);
        ReflectionTestUtils.setField(inMemoryLocalCache, "capacity", 100);
        ReflectionTestUtils.setField(inMemoryLocalCache, "lowCacheMarkPercentage", 20);
        ReflectionTestUtils.setField(inMemoryLocalCache, "pollTimeoutMs", 300);
        ReflectionTestUtils.setField(inMemoryLocalCache, "startupTimeoutSeconds", 20);
    }

    private List<String> generateHashes(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> "hash" + i)
                .toList();
    }

    @Nested
    @DisplayName("Cache initialization tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize and fill the cache to capacity on startup")
        void initializeCache_FillsToCapacity() {
            List<String> initialHashes = generateHashes(100);
            when(hashDao.getHashBatch(100)).thenReturn(initialHashes);

            inMemoryLocalCache.initializeCache();

            BlockingQueue<String> queue =
                    (BlockingQueue<String>) ReflectionTestUtils.getField(inMemoryLocalCache, "hashQueue");
            assertNotNull(queue);
            assertEquals(100, queue.size());
            assertTrue(queue.containsAll(initialHashes));
            verify(hashDao).getHashBatch(100);
        }

        @Test
        @DisplayName("Should handle empty hash batch from repository during initialization")
        void initialization_HandleEmptyBatch() {
            when(hashDao.getHashBatch(100)).thenReturn(Collections.emptyList());
            inMemoryLocalCache.initializeCache();
            BlockingQueue<String> queue =
                    (BlockingQueue<String>) ReflectionTestUtils.getField(inMemoryLocalCache, "hashQueue");
            assertNotNull(queue);
            assertTrue(queue.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Hash tests")
    class GetHashTests {

        @BeforeEach
        void populateCache() {
            BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>(100);
            hashQueue.addAll(generateHashes(50));
            ReflectionTestUtils.setField(inMemoryLocalCache, "hashQueue", hashQueue);
        }

        @Test
        @DisplayName("Shold retrieve a hash successfully from a populated cache")
        void getHash_Success() {
            String hash = inMemoryLocalCache.getHash();
            BlockingQueue<String> queue =
                    (BlockingQueue<String>) ReflectionTestUtils.getField(inMemoryLocalCache, "hashQueue");

            assertEquals("hash0", hash);
            assertEquals(49, Objects.requireNonNull(queue).size());
        }
    }

    @Nested
    @DisplayName("Cache Refill Trigger tests")
    class RefillTriggerTests {

        @Test
        @DisplayName("Should trigger refill when hash count drops to the low-mark")
        void getHash_TriggersRefill() {
            BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>(100);
            hashQueue.addAll(generateHashes(21));
            ReflectionTestUtils.setField(inMemoryLocalCache, "hashQueue", hashQueue);
            when(hashDao.getHashBatch(anyInt())).thenReturn(generateHashes(80));

            inMemoryLocalCache.getHash();

            verify(hashDao).getHashBatch(80);
            assertEquals(100, hashQueue.size());
        }

        @Test
        @DisplayName("Should not trigger refill when hash count is above the low-mark")
        void getHash_NotRefill() {
            BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>(100);
            hashQueue.addAll(generateHashes(22));
            ReflectionTestUtils.setField(inMemoryLocalCache, "hashQueue", hashQueue);

            inMemoryLocalCache.getHash();

            verify(hashDao, never()).getHashBatch(anyInt());
            assertEquals(21, hashQueue.size());
        }

        @Test
        @DisplayName("Should not trigger refill if a refill is already in progress")
        void getHash_NoRefillIfAlreadyReplenishing() {
            BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>(100);
            hashQueue.addAll(generateHashes(20));
            ReflectionTestUtils.setField(inMemoryLocalCache, "hashQueue", hashQueue);
            AtomicBoolean isReplenishing =
                    (AtomicBoolean) ReflectionTestUtils.getField(inMemoryLocalCache, "isReplenishing");
            Objects.requireNonNull(isReplenishing).set(true);

            inMemoryLocalCache.getHash();

            verify(hashDao, never()).getHashBatch(anyInt());
        }
    }
}