package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <h2>Задание</h2>
 * <div>Реализовать класс HashCache, который осуществляет внутреннее кэширование свободных хэшей, асинхронно
 * заполняет этот кэш без блокирования запросов и является полностью потокобезопасным</div>
 * <h2>Критерии приема</h2>
 * <li>HashCache — Spirng bean с соответствующими аннотациями.</li>
 * <li>HashCache хранит себе структуру данных, в которой и будут кэшироваться хэши. Она должна быть
 * потокобезопасной и иметь удобный интерфейс для получения ровно одного случайного хэша. Используем
 * готовое решение из пакета concurrency. Нужно подумать, какую структуру данных выбрать. Подсказка:
 * это не ConcurrentHashMap. Её размер должен определяться из конфига.</li>
 * <li>HashCache содержит в себе ExecutorService бин для запуска асинхронных задач.</li>
 * <li>Этот ExecutorService бин создаётся в отдельной конфигурации бинов. Его размер и размер его
 * очереди задач задаётся из конфига.</li>
 * <li>Если количество доступных элементов в структуре данных внутри HashCache больше 20% от её
 * максимального размера, то метод getHash() в классе HashCache просто возвращает первый элемент на
 * поверхности этой коллекции. Значение процентов хранится в конфиге.</li>
 * <li>Если количество элементов в ней менее 20%, то РОВНО ОДИН РАЗ, асинхронно через ExecutorService
 * запускается получение хэшей из HashRepository, и заполнение ими внутренней коллекции HashCache.
 * РОВНО ОДИН РАЗ значит, что никакой другой поток не должен ещё раз запустить эту активность,
 * пока она не завершилась полностью после предыдущего запуска. Т.е. здесь должен быть механизм
 * эксклюзивного доступа. Также здесь происходит асинхронный запуск HashGenerator, который
 * генерирует дополнительные хэши в БД, раз уж мы потащили пачку в память.</li>
 * <p>
 * Используются аннотации lombok.</li>
 */
@Slf4j
@Service
public class HashCache {

    private static final int safeDelta = 5;

    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;
    private final AtomicBoolean lock = new AtomicBoolean(false);

    private final Long maxAmount;
    private final int minPercent;
    private final Queue<String> hashQueue;

    public HashCache(
        HashGenerator hashGenerator,
        ExecutorService executorService,
        @Value("${app.cache.max-amount:10}") Long maxAmount,
        @Value("${app.min-percent:3}") int minPercent
    ) {
        this.hashGenerator = hashGenerator;
        this.executorService = executorService;
        this.maxAmount = maxAmount;
        this.minPercent = minPercent;
        int capacity = (int) (maxAmount + (maxAmount * minPercent / 100) + safeDelta);
        this.hashQueue = new ArrayBlockingQueue<>(capacity);
    }

    @PostConstruct
    public void init() {
        log.info("Init class HashCache");
        if (isNeedExtend()) {
            log.debug("Need extend cache. Filling the cache synchronously");
            fillCache();
        }
    }

    public String getNewHash() {
        do {
            String hash = hashQueue.poll(); // String hash = setOps().pop(FREE_HASH);
            if (hash == null) {
                if (lock.compareAndExchange(false, true)) {
                    log.info("Redis Hash Cache is empty. Filling the cache Synchronously.");
                    fillCache();
                    lock.set(false);
                }
            } else {
                if (isNeedExtend()) {
                    log.debug("NEED extend cache. lock is: {}", lock.get());
                    if (lock.compareAndSet(false, true)) {
                        log.debug("Filling the cache Asynchronously. before start CompletableFuture.supplyAsync");
                        CompletableFuture.supplyAsync(
                                () -> {
                                    log.debug("Filling the cache Asynchronously. Into process. maxAmount is: {}",
                                        maxAmount);
                                    return hashGenerator.getHashes(maxAmount);
                                }, executorService)
                            .thenAccept(hashes -> {
                                log.debug("Filling the cache Asynchronously. hashes: {}", hashes);
                                addHashPortion(hashes);
                            })
                            .thenRun(() -> {
                                log.debug("before restore flag lock. lock is: {}", lock.get());
                                lock.set(false);
                                log.debug("restored flag lock. lock is: {}", lock.get());
                            });
                    }
                    log.debug("after check lock status. lock is: {}", lock.get());
                } else {
                    log.debug("NO NEED extend cache. lock is: {}", lock.get());
                }
                return hash;
            }
        } while (true);
    }

    private boolean isNeedExtend() {
        int cacheSize = hashQueue.size();
        log.debug("free hash size: {}", cacheSize);
        return cacheSize < (maxAmount * minPercent / 100);
    }

    private void fillCache() {
        List<String> freeHashes = hashGenerator.getHashes(maxAmount);
        addHashPortion(freeHashes);
    }

    private void addHashPortion(List<String> freeHashes) {
        log.debug("Adding Hash Portion. Portion: {}", freeHashes);
        hashQueue.addAll(freeHashes);
    }
}
