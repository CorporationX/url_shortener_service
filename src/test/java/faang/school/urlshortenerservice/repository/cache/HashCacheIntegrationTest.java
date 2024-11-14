package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HashCacheIntegrationTest {

    @SpyBean
    private HashCache hashCache;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService executorService;

    //    @Value("${hash_cache.capacity}")
    private int capacity = 3;

    @Value("${hash_cache.remainder_percent}")
    private int collectionRemainderPercent;

    @Captor
    private ArgumentCaptor<ArrayBlockingQueue<String>> queueCaptor;

    @BeforeEach
    public void setUp() throws Exception {
        // Устанавливаем `executorService` как mock, чтобы избежать реальных вызовов
        Field executorServiceField = HashCache.class.getDeclaredField("executorService");
        executorServiceField.setAccessible(true);
        executorServiceField.set(hashCache, executorService);

        // Инициализируем `cache` с помощью рефлексии, чтобы управлять его содержимым
        Field cacheField = HashCache.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Queue<String> testCache = new ArrayDeque<>();
        cacheField.set(hashCache, testCache);

        // Настраиваем поведение hashRepository для теста
        when(hashRepository.getHashBatch(capacity)).thenReturn(List.of("hash1", "hash2", "hash3"));
    }

    @Test
    public void testInitCache() throws Exception {
        when(hashRepository.getHashBatch(capacity)).thenReturn(List.of("hash1", "hash2", "hash3"));
        // Проверяем, что `cache` инициализируется с данными из `hashRepository`


        Field cacheField = HashCache.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Queue<String> cache = (Queue<String>) cacheField.get(hashCache);

        verify(queueCaptor.capture().addAll(List.of("hash1", "hash2", "hash3")),times(1));
//        assertThat(cache).containsExactly("hash1", "hash2", "hash3");
//        System.out.println(queueCaptor.getValue());
    }

    @Test
    public void testGetHashTriggersAddAllWhenBelowThreshold() throws Exception {
        // Настройка количества хешей в базе данных ниже порога генерации
        when(hashRepository.count()).thenReturn(50L);

        // Устанавливаем количество хешей в `cache` ниже порога `collectionRemainderPercent`
        Field cacheField = HashCache.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Queue<String> cache = (Queue<String>) cacheField.get(hashCache);
        cache.clear(); // Очищаем для проверки триггера
        cache.add("hash_stub"); // Добавляем несколько элементов для теста

        // Настраиваем `getHashBatch` для добавления дополнительных данных в `cache`
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hash4", "hash5"));

        // Запускаем `getHash`, чтобы проверить вызов `cache.addAll`
        for (int i = 0; i < 3333; i++) {
            hashCache.getHash();
        }

        // Проверяем, что cache обновился с новыми значениями
        assertThat(cache).contains("hash_stub", "hash4", "hash5");
    }

    @Test
    public void testGenerateBatchTriggeredWhenDatabaseBelowCapacity() {
        // Настройка так, чтобы сработал `hashGenerator.generateBatch()`
        when(hashRepository.count()).thenReturn(30L);
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hash6", "hash7"));

        hashCache.getHash();

        // Проверяем, что генерация запущена
        verify(hashGenerator, times(1)).generateBatch();
    }
}
