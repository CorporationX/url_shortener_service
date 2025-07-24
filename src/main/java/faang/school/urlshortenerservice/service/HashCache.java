package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.redis.AtomicReferenceConfig;
import faang.school.urlshortenerservice.config.redis.RedisConfig;
import faang.school.urlshortenerservice.entity.Hash;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

@Service
@Slf4j
public class HashCache {
    private static final String KEY = "hash_cache";
    private final HashService hashService;
    private final RedisConfig redisConfig;
    private final ThreadPoolTaskExecutor executorService;
    private final AtomicReferenceArray<Hash> hashAtomicReference;
    private final Integer sizeAtomicReference;

    public HashCache(HashService hashService, RedisConfig redisConfig,
                     @Qualifier("taskExecutorHashCache") ThreadPoolTaskExecutor executorService,
                     AtomicReferenceArray<Hash> hashAtomicReference,
                     AtomicReferenceConfig config) {
        this.hashService = hashService;
        this.redisConfig = redisConfig;
        this.executorService = executorService;
        this.hashAtomicReference = hashAtomicReference;
        this.sizeAtomicReference = config.getSize();
    }

    public RedissonClient connectionToRedis() {
        Config config = new Config();
        config.useSingleServer()
                .setPassword(redisConfig.getPassword())
                .setAddress("redis://127.0.0.1:6379");

        return Redisson.create(config);
    }

    public void saveToRedisHash() {
        List<Hash> hashes = hashService.deleteHashFromDataBase();

        for (int i = 0; i < hashes.size(); i++) {
            hashAtomicReference.set(i, hashes.get(i));
        }
        RedissonClient redisson = connectionToRedis();

        RList<Hash> hashRList = redisson.getList(KEY);

        addedToRedis(hashes, hashRList);
    }

    public void checkMemoryRedis () {
        RList<Hash> hashRList = connectionToRedis().getList(KEY);

        if (hashRList.size() < sizeAtomicReference * 0.20) {
            executorService.submit(() -> {
                List<Hash> hashes = hashService.deleteHashFromDataBase();
                for (int i = hashRList.size(); i < hashes.size(); i++) {
                    hashAtomicReference.set(i, hashes.get(i));
                }
                addedToRedis(hashes, hashRList);
            });
        }
    }

    private void addedToRedis(List<Hash> hashes, RList<Hash> hashRList) {
        for (int i = 0; i < hashes.size(); i++) {
            Hash hash = hashAtomicReference.get(i);
            if (hash != null) {
                hashRList.add(hash);
            }
        }
    }

}


/*
HashCache хранит себе структуру данных, в которой и будут кэшироваться хэши.
Она должна быть потокобезопасной и иметь удобный интерфейс для получения ровно одного случайного хэша.
Используем готовое решение из пакета concurrency.
Нужно подумать, какую структуру данных выбрать. Подсказка: это не ConcurrentHashMap.
Её размер должен определяться из конфига.

HashCache содержит в себе ExecutorService бин для запуска асинхронных задач.

Этот ExecutorService бин создаётся в отдельной конфигурации бинов.
Его размер и размер его очереди задач задаётся из конфига.

Если количество доступных элементов в структуре данных внутри HashCache больше 20% от её максимального размера,
то метод getHash() в классе HashCache просто возвращает первый элемент на поверхности этой коллекции.
Значение процентов хранится в конфиге.

Если количество элементов в ней менее 20%, то РОВНО ОДИН РАЗ,
асинхронно через ExecutorService запускается получение хэшей из HashRepository,
и заполнение ими внутренней коллекции HashCache. РОВНО ОДИН РАЗ значит,
что никакой другой поток не должен ещё раз запустить эту активность,
пока она не завершилась полностью после предыдущего запуска.
Т.е. здесь должен быть механизм эксклюзивного доступа.
Также здесь происходит асинхронный запуск HashGenerator, который генерирует дополнительные хэши в БД,
раз уж мы потащили пачку в память.
 */