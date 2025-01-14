package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${hashCash.queueCapacity}")
    private int queueCapacity;
    @Value("${hashCash.percent}")
    private double percent;
    @Value("${hashCash.redisBatchSize}")
    private int redisBatchSize;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final ThreadPoolTaskExecutor taskExecutor;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private BlockingQueue<Hash> caches;

    @PostConstruct
    public void init() {
        caches = new ArrayBlockingQueue<>(queueCapacity);
        hashGenerator.generateBatch();
        caches.addAll(hashRepository.getHashBatch(redisBatchSize));
    }

//    public Hash getHash() {
//        synchronized (this) {
//            int threshold = (int) (cache.size() * percent);
//
//            if (cache.size() < threshold) {
//                try {
//                    return cache.take();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            if (isFilling.compareAndSet(false, true)) {
//                asyncExecutor.asyncExecutor().execute(() -> {
//                    try {
//                        fillCacheFromRepository();
//                        generateAdditionalHashes();
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    } finally {
//                        isFilling.set(false);
//                    }
//                });
//            }
//        }
//
//        return cache.poll();
//    }
//
//    private void fillCacheFromRepository() {
//        try {
//            int missingHashes = queueCapacity - cache.size();
//            var newHashes = hashRepository.getHashBatch(missingHashes);
//
//            cache.addAll(newHashes);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void generateAdditionalHashes() {
//        try {
//            hashGenerator.generateBatch();
//            var generatedHashes = hashRepository.getHashBatch(redisBatchSize);
//
//            hashRepository.saveAll(generatedHashes);
//            cache.addAll(generatedHashes);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


    public Hash getHash() {
        if (caches.size() <= redisBatchSize * percent) {
            if (isFilling.compareAndSet(false, true)) {
                CompletableFuture.runAsync(() -> {
                    try {
                        hashGenerator.generateBatch();
                        caches.addAll(hashRepository.getHashBatch(redisBatchSize));
                    } catch (Exception e) {
                        throw new RuntimeException("Something wrong: " + e.getMessage());
                    } finally {
                        isFilling.set(false);
                    }
                }, taskExecutor);
            }
        }
        try {
            return caches.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("что то отвалилось пока ждали хеш : " + e.getMessage(), e.getCause());
        }
    }
}
