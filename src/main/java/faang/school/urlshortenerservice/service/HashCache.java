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
                //.setPassword(redisConfig.getPassword())
                .setAddress("redis://" + redisConfig.getHost() + ":" + redisConfig.getPort());

        return Redisson.create(config);
    }

    public void saveToRedisHash() {
        List<Hash> hashes = hashService.deleteHashFromDataBase();

        RedissonClient redisson = connectionToRedis();

        RList<Hash> hashRList = redisson.getList(KEY);

        addedToRedis(hashes, hashRList);
    }

    public void checkMemoryRedis() {
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
        for (Hash hash : hashes) {
            if (hash != null) {
                hashRList.add(hash);
            }
        }
    }

}
