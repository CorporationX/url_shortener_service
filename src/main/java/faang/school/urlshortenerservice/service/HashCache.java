package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Компонент для кэширования и управления свободными хэшами, которые используются при генерации коротких URL.
 * <p>
 * Хранит хэши в памяти в виде очереди с ограниченным размером. При инициализации заполняется из {@link HashService}.
 * При каждом запросе хэша проверяется, не нужно ли дополнительно пополнить кэш.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashService hashService;

    @Value("${hash-generator.cache-size}")
    private int cacheSize;
    @Value("${hash-generator.min-free-ratio-hashes}")
    private double cacheFreeRation;
    private Queue<String> cache;

    /**
     * Метод инициализации компонента. Вызывается автоматически после создания бина.
     * Загружает в память стартовый набор хэшей из {@link HashService}.
     */
    @PostConstruct
    public void init() {
        log.debug("Initializing hash cache");
        cache = new ArrayBlockingQueue<>(cacheSize);
        List<String> hashes = hashService.getHashes(cacheSize);
        cache.addAll(hashes);
        log.debug("Hash cache size: {}", cache.size());
        log.debug("Hash cache initialized");
    }

    /**
     * Получает свободный хэш из кэша.
     * Если количество хэшей в кэше становится ниже установленного порога — запускает процесс пополнения.
     *
     * @return свободный хэш, или {@code null}, если очередь пуста (что маловероятно при корректной настройке)
     */
    public String getHash() {
        if (mustFillCacheInDb()) {
            fillCache();
        }

        return cache.poll();
    }

    private void fillCache() {
        log.debug("Start filling cache");
        List<String> hashes = hashService.getHashes(cacheSize - cache.size());
        cache.addAll(hashes);
        log.debug("Hash cache filled");
    }

    private boolean mustFillCacheInDb() {
        double ratio = (double) cache.size() / cacheSize;

        return ratio < cacheFreeRation;
    }
}
