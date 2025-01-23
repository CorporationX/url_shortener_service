package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.exception.CacheInitializationException;
import faang.school.urlshortenerservice.exception.HashCacheException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {
    private final HashGenerator generator;
    private final int capacity;
    private final double fillPercent;
    private final AtomicBoolean filling;
    private final Queue<String> hashes;

    public HashCache(HashGenerator generator,
                     @Value("${spring.data.capacity:10}") int capacity,
                     @Value("${spring.data.fill_percent}") double fillPercent
    ) {
        this.generator = generator;
        this.capacity = capacity;
        this.fillPercent = fillPercent;
        this.filling = new AtomicBoolean();
        this.hashes = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * Инициализирует кэш хэшей при старте приложения.
     *
     * @throws CacheInitializationException Если произошла ошибка при инициализации кэша.
     */
    @PostConstruct
    public void init() {
        log.info("Инициализация кэша хэшей...");

        try {
            hashes.addAll(generator.getHashBatch(capacity));
            log.info("Кэш хэшей успешно инициализирован. Загружено {} хэшей.", capacity);
        } catch (Exception e) {
            log.error("Ошибка при инициализации кэша хэшей: {}", e.getMessage(), e);
            throw new CacheInitializationException("Ошибка при инициализации кэша хэшей: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает хэш из кэша. Если кэш заполнен менее чем на fillPercent, запускает асинхронное пополнение.
     *
     * @return Хэш из кэша.
     * @throws HashCacheException Если произошла ошибка при получении хэша.
     */
    public String getHash() {
        log.debug("Запрос на получение хэша из кэша. Текущий размер кэша: {}", hashes.size());

        try {
            if (getHashesCapacity() < fillPercent) {
                if (filling.compareAndSet(false, true)) {
                    log.info("Кэш заполнен менее чем на {}%. Запуск асинхронного пополнения...", fillPercent);
                    generator.getHashBatchAsync(getFreeCapacity())
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false))
                        .exceptionally(e -> {
                            log.error("Ошибка при пополнении кэша: {}", e.getMessage(), e);
                            filling.set(false);
                            return null;
                        });
                }
            }

            String hash = hashes.poll();
            if (hash == null) {
                log.error("Кэш хэшей пуст. Невозможно получить хэш.");
                throw new HashCacheException("Кэш хэшей пуст. Невозможно получить хэш.");
            }

            log.debug("Хэш успешно получен из кэша: {}", hash);
            return hash;
        } catch (Exception e) {
            log.error("Ошибка при получении хэша из кэша: {}", e.getMessage(), e);
            throw new HashCacheException("Ошибка при получении хэша из кэша: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает свободную ёмкость кэша.
     *
     * @return Количество хэшей, которые можно добавить в кэш.
     */
    private int getFreeCapacity() {
        return (int) (capacity * (100 - fillPercent / 100.0));
    }

    /**
     * Возвращает текущий процент заполнения кэша.
     *
     * @return Процент заполнения кэша.
     */
    private double getHashesCapacity() {
        return hashes.size() / (capacity / 100.0);
    }
}
