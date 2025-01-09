package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
public class HashCache {

    private final HashService hashService;
    private final HashGenerator hashGenerator;

    /**
     * Максимальный размер кэша (например, 1000).
     * Значение берётся из application.yaml: app.hash_cache.hashes_max_size
     */
    @Value("${app.hash_cache.hashes_max_size:1000}")
    private int hashesMaxSize;

    /**
     * Минимальный порог, при котором начинаем асинхронное пополнение (например, 200).
     * Значение берётся из application.yaml: app.hash_cache.hashes_min_size
     */
    @Value("${app.hash_cache.hashes_min_size:200}")
    private int hashesMinSize;

    /**
     * Потокобезопасная очередь, где храним хэши.
     */
    private final Queue<String> cacheQueue = new ConcurrentLinkedDeque<>();

    /**
     * Флаг, чтобы не запускать "пополнение кэша" одновременно из нескольких потоков.
     */
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    /**
     * Локальный пул потоков, который будет выполнять асинхронные задачи.
     * (Можно заменить на @Async и ThreadPoolTaskExecutor, если вам удобнее так.)
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public HashCache(HashService hashService, HashGenerator hashGenerator) {
        this.hashService = hashService;
        this.hashGenerator = hashGenerator;

        // При создании бина можете сразу один раз наполнить кэш, если хотите.
        // Например, чтобы при старте приложения уже был запас хэшей.
        isUpdating.set(true);
        try {
            refillCache();  // Синхронная начальная загрузка
        } finally {
            isUpdating.set(false);
        }
    }

    /**
     * Основной метод для получения хэша из кэша.
     *
     * @return свободный хэш (или null, если почему-то кэш опустел)
     */
    public String getHash() {
        // 1) Проверяем, не пора ли асинхронно пополнить кэш
        checkAndRefillIfNeeded();

        // 2) Отдаём первый элемент из очереди
        return cacheQueue.poll();
    }

    /**
     * Если размер очереди меньше заданного порога,
     * то асинхронно запускаем пополнение из БД и генерацию новых хэшей.
     */
    private void checkAndRefillIfNeeded() {
        if (cacheQueue.size() < hashesMinSize) {
            // compareAndSet(false, true) — значит, только один поток зайдёт сюда и поставит флаг в true
            if (isUpdating.compareAndSet(false, true)) {
                executorService.submit(() -> {
                    try {
                        // Сначала пополняем локальный кэш хэшами из таблицы hash
                        refillCache();

                        // Параллельно генерируем ещё хэшей в БД
                        // (чтобы в будущем было, что брать)
                        hashGenerator.generate();
                    } catch (Exception e) {
                        log.error("Error while refilling HashCache", e);
                    } finally {
                        // Снимаем флаг, позволяя другим потокам снова проверять
                        isUpdating.set(false);
                    }
                });
            }
        }
    }

    /**
     * Запрашивает из БД партию хэшей (до достижения maxSize) и кладёт их в очередь.
     */
    private void refillCache() {
        int needCount = hashesMaxSize - cacheQueue.size();
        if (needCount > 0) {
            log.info("Refilling HashCache with {} hashes...", needCount);

            // Метод findAllByPackSize вернёт нужное кол-во хэшей
            // (или меньше, если их в таблице не хватает).
            List<String> newHashes = hashService.findAllByPackSize(needCount);

            // Складываем их в очередь.
            cacheQueue.addAll(newHashes);

            log.info("HashCache refilled. Current size = {}", cacheQueue.size());
        }
    }
}