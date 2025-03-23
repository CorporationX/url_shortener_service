package faang.school.urlshortenerservice.cashe;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final ExecutorService hashExecutor;
    private final Queue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isFetching = new AtomicBoolean(false);


    @Value("${data.url.cache.size}")
    private int cacheSize;

    @Value("${data.url.cache.threshold}")
    private double threshold;

    @PostConstruct
    public void init() {
        try {
            List<Hash> initialHashes = hashGenerator.getHashes();
            for (Hash hashEntity : initialHashes) {
                if (hashQueue.size() < cacheSize) {
                    hashQueue.offer(hashEntity.getHash());
                } else {
                    break;
                }
            }
            log.info("HashCache успешно инициализирован. Размер кэша: {}", hashQueue.size());
        } catch (Exception e) {
            log.error("Ошибка при инициализации HashCache", e);
            throw new IllegalStateException("Ошибка при инициализации HashCache", e);
        }
    }

    public String getHash() {
        if (hashQueue.size() <= threshold * cacheSize) {
            triggerAsyncFetch();
        }
        String hash = hashQueue.poll();
        if (hash == null) {
            List<Hash> newHashes = hashGenerator.getHashes();
            newHashes.forEach(h -> {
                if (hashQueue.size() < cacheSize) {
                    hashQueue.offer(h.getHash());
                }
            });
            hash = hashQueue.poll();
        }
        return hash;
    }

    private void triggerAsyncFetch() {
        if (isFetching.compareAndSet(false, true)) {
            hashExecutor.submit(() -> {
                try {
                    List<Hash> hashes = hashGenerator.getHashes();
                    if (hashes.isEmpty()) {
                        log.warn("Хэши не найдены, генерируем новые");
                        hashGenerator.generateBatch();
                        hashes = hashGenerator.getHashes();
                    }

                    for (Hash hashEntity : hashes) {
                        if (hashQueue.size() < cacheSize) {
                            hashQueue.offer(hashEntity.getHash());
                        } else {
                            break;
                        }
                    }
                } catch (Exception ex) {
                    throw new HashCacheException("Ошибка при асинхронном обновлении кеша", ex);
                } finally {
                    isFetching.set(false);
                }
            });
        }
    }
}