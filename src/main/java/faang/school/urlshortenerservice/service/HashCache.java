package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.ValidationException;
import faang.school.urlshortenerservice.util.HashFabricator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {

    @Value("${hash.cache.capacity}")
    private int hashCapacity;
    @Value("${hash.cache.load.factor}")
    private int hashLoadFactor;

    private final AtomicBoolean checkUpdate;
    private final BlockingQueue<String> queueHashes;
    private final HashFabricator hashFabricator;

    public HashCache(@Value("${hash.cache.capacity}") int hashCapacity, HashFabricator hashFabricator) {
        if (hashCapacity <= 0) {
            throw new ValidationException("SIZE MUST BE POSITIVE!");
        }
        this.hashCapacity = hashCapacity;
        this.hashFabricator = hashFabricator;
        this.checkUpdate = new AtomicBoolean(false);
        this.queueHashes = new ArrayBlockingQueue<>(hashCapacity);
    }

    @PostConstruct
    void start() {
        queueHashes.addAll(hashFabricator.getHashes(hashCapacity));
    }

    @Async("shortLinkThreadPoolExecutor")
    public void updateCache() {
        int currentFullness = queueHashes.size() * 100 / (hashCapacity);
        if (currentFullness < hashLoadFactor && checkUpdate.compareAndSet(false, true)) {
            hashFabricator.getHashesAsync(hashCapacity).thenAccept(queueHashes::addAll).thenRun(() -> checkUpdate.set(false));
        }
    }

    public String getHash() {
        updateCache();
        return queueHashes.poll();
    }
}
