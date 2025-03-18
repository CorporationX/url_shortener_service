package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.LocalCacheProperties;
import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCache {
    private final HashGenerator hashGenerator;
    private final Executor hashGeneratorExecutor;
    private final LocalCacheProperties properties;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final Lock lock = new ReentrantLock();
    private BlockingQueue<Hash> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(properties.getCapacity());
        fillCacheSync(properties.getCapacity());
        log.info("LocalHash initialization completed.");
    }

    public String getHash() {
        if (hashes.remainingCapacity() > (properties.getCapacity() * (100 - properties.getFillPercentage()) / 100)) {
            if (isFilling.compareAndSet(false, true)) {
                log.info("Start fill cache async");
                fillCacheAsync(properties.getCapacity());
            }
        }
        lock.lock();
        Hash hash = hashes.poll();
        lock.unlock();
        if (hash == null) {
            throw new RuntimeException("No hashes available in the cache");
        }
        return hash.getHash();
    }

    public void fillCacheSync(int amount) {
        List<Hash> newHashes = hashGenerator.getHashes(amount);
        hashes.addAll(newHashes);
        //hashes.addAll(hashGenerator.getHashes(properties.getCapacity()));
    }

    public void fillCacheAsync(int amount) {
        CompletableFuture.supplyAsync(() -> hashGenerator.getHashes(amount), hashGeneratorExecutor)
                .thenAccept(newHashes -> {
                    //synchronized (hashes)
                    {
                        lock.lock();
                        hashes.addAll(newHashes);
                        lock.unlock();
                    }
                })
                .exceptionally(ex -> {
                    log.error("Failed to fill the cache with new hashes", ex);
                    return null;
                })
                .thenRun(() -> isFilling.set(false));
    }
}

/*
        if (hashes.size() < (properties.getCapacity() * properties.getFillPercentage() / 100)) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(properties.getCapacity())
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        }*/
        /*
        // Проверяем, нужно ли заполнять кэш
        if (hashes.remainingCapacity() > (properties.getCapacity() * (100 - properties.getFillPercentage()) / 100)) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(properties.getCapacity())
                        .thenAccept(newHashes -> {
                            synchronized (hashes) {
                                hashes.addAll(newHashes);
                            }
                        })
                        .exceptionally(ex -> {
                            log.error("Failed to fill the cache with new hashes", ex);
                            return null;
                        })
                        .thenRun(() -> isFilling.set(false));
            }
        }
         */