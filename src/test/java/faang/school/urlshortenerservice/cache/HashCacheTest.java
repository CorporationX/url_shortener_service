package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.properties.short_url.HashProperties;
import faang.school.urlshortenerservice.util.hash_generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    private static final int queueCapacity = 10;

    @Mock
    private ExecutorService executorService;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashProperties hashProperties;

    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        when(hashProperties.getCacheCapacity()).thenReturn(queueCapacity);
        hashCache = new HashCache(executorService, hashGenerator, hashProperties);
    }

    @Test
    void initCacheTest() throws NoSuchFieldException, IllegalAccessException {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.getHashes(queueCapacity)).thenReturn(hashes);

        hashCache.initCache();

        Queue<String> cacheQueue = getHashCacheQueue();

        assertFalse(cacheQueue.isEmpty());
        assertEquals(hashes.size(), cacheQueue.size());
        verify(hashGenerator, times(1)).getHashes(queueCapacity);
    }

    @Test
    void getHashWithoutFillingTest() {
        int minPercentageThreshold = 10;
        Queue<String> cacheQueue = getHashCacheQueue();
        cacheQueue.add("Ju");
        cacheQueue.add("W7E");
        when(hashProperties.getMinPercentageThreshold()).thenReturn(minPercentageThreshold);

        String freeHash = hashCache.getHash();

        assertNotNull(freeHash);
        assertEquals("Ju", freeHash);
        assertEquals("W7E", cacheQueue.peek());
    }

    @Test
    void testNeedToFillQueue() {
        int minPercentageThreshold = 20;
        Queue<String> cacheQueue = getHashCacheQueue();
        cacheQueue.add("Ju");
        cacheQueue.add("W7E");
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.getHashes(queueCapacity)).thenReturn(hashes);
        when(hashProperties.getMinPercentageThreshold()).thenReturn(minPercentageThreshold);

        String freeHash = hashCache.getHash();

        assertNotNull(freeHash);
        assertEquals("Ju", freeHash);
        assertEquals("W7E", cacheQueue.peek());
    }

//    @Test
//    void testRunAsyncFillingQueue() throws InterruptedException {
//        // Подготовка моков
//        when(hashProperties.getCacheCapacity()).thenReturn(10);
//        when(hashProperties.getMinPercentageThreshold()).thenReturn(30);
//
//        // Инициализация очереди с мокированными хешами
//        hashCache.freeHashesQueue.add("hash1");
//
//        // Мокируем асинхронное выполнение
//        Future<?> future = mock(Future.class);
//        when(executorService.submit(any(Runnable.class))).thenReturn(future);
//
//        // Вызов getHash, чтобы триггерить обновление очереди
//        hashCache.getHash();
//
//        // Проверка того, что задача была отправлена в ExecutorService
//        verify(executorService, times(1)).submit(any(Runnable.class));
//    }
//
//    @Test
//    void testAsyncQueueUpdate() throws InterruptedException {
//        // Подготовка моков
//        when(hashProperties.getCacheCapacity()).thenReturn(10);
//        when(hashProperties.getMinPercentageThreshold()).thenReturn(30);
//
//        // Мокируем асинхронную работу
//        Future<?> future = mock(Future.class);
//        when(executorService.submit(any(Runnable.class))).thenReturn(future);
//
//        // Проверка асинхронной работы
//        hashCache.runAsyncFillingQueue();
//
//        // Проверка того, что состояние isQueueBeingUpdated было сброшено
//        assertFalse(hashCache.isQueueBeingUpdated.get());
//    }
//
//    @Test
//    void testQueueIsNotBeingUpdated() {
//        // Проверка того, что флаг isQueueBeingUpdated устанавливается верно
//        assertTrue(hashCache.isQueueBeingUpdated.compareAndSet(false, true));
//        assertFalse(hashCache.isQueueBeingUpdated.compareAndSet(false, true));  // После первого вызова флаг не может быть изменен
//    }

    private Queue<String> getHashCacheQueue() {
        try {
            Class<HashCache> clazz = HashCache.class;
            Field queueField = clazz.getDeclaredField("freeHashesQueue");
            queueField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Queue<String> queue = (Queue<String>) queueField.get(hashCache);
            return queue;
        } catch (Exception e) {
            return null;
        }
    }
}