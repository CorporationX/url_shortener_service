package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
@RequiredArgsConstructor
public class HashCache {

    private static final String FREE_HASH = "FREE_HASH";

    @Value("${app.cache.max-amount:10}")
    private Long maxAmount;
    @Value("${app.min-percent:3}")
    private int minPercent;

    private final AtomicBoolean lock = new AtomicBoolean(false);

    private final HashGenerator hashGenerator;
    private final StringRedisTemplate stringRedisTemplate;
    private final ExecutorService executorService;

    @PostConstruct
    public void init() {
        log.info("Init class HashCache");
        if (isNeedExtend()) {
            log.debug("Need extend cache. Filling the cache synchronously");
            fillCache();
        }
    }

    public String getNewHash() {
        /* todo:
         *  - достать значение из кеша с хешами
         *  - проверить процент заполнения кеша и если оно меньше запустить процесс наполнения из БД
         *  - что делать если кеш пустой?
         *  - что делать если и в таблице hashes нет сгенерированных хешей?
         */
        do {
            String hash = setOps().pop(FREE_HASH);
            if (hash == null) {
                if (lock.compareAndExchange(false, true)) {
                    log.info("Redis Hash Cache is empty. Filling the cache Synchronously.");
                    /* todo: попробовать вычитать данные из HashGenerator через метод getHashes(maxAmount);
                     */
                    fillCache();
                    lock.set(false);
                }
            } else {
                /* todo: проверить процент заполнения кеша и если он меньше запустить процесс наполнения из БД
                 */
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
        Long cacheSize = Objects.requireNonNullElse(setOps().size(FREE_HASH), 0L);
        log.debug("free hash size: {}", cacheSize);
        return cacheSize < (maxAmount * minPercent / 100);
    }

    private void fillCache() {
        List<String> freeHashes = hashGenerator.getHashes(maxAmount);
        addHashPortion(freeHashes);
    }

    private SetOperations<String, String> setOps() {
        return stringRedisTemplate.opsForSet();
    }

    private void addHashPortion(List<String> freeHashes) {
        log.debug("Adding Hash Portion. Portion: {}", freeHashes);
        setOps().add(FREE_HASH, freeHashes.toArray(new String[0]));
    }

    // private List<String> getPortionOfHashes(Long range) {
    //     do {
    //         List<String> portion = hashRepository.getPortionOfHashes(maxAmount);
    //         if (portion.isEmpty()) {
    //             hashGenerator.generateBatch();
    //         } else {
    //             log.debug("getting hashes. portion size is: {}", portion.size());
    //             return portion;
    //         }
    //     } while (true);
    // }
}
