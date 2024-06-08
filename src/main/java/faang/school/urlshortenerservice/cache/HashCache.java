package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.hash.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${cache.capacity}")
    private int sizeQueue;
    @Value("${cache.fullness}")
    private double fullness;
    private  Queue<String> cache;

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        log.info("Начинается заполнение кэша");
        cache = new ArrayBlockingQueue<>(sizeQueue);
        cache.addAll(hashGenerator.getHashes(sizeQueue));
        log.info("Кэш заполнен хэшами");
    }

    public String getHash() {
        log.info("Запрос на получение хэша из кэша");
        updateCache();
        String hash = cache.poll();
        log.info("Хэш получен {}", hash);
        return hash;
    }
    @Async("shortenerService")
    public void updateCache() {
        double percent = (double) cache.size() / 100;
        if (percent < fullness && isFilling.compareAndSet(false, true)) {
            log.info("Запуск наполнения кэша данными");
            hashGenerator.generatedBatchAsync(sizeQueue)
                    .thenAccept(hashes -> cache.addAll(hashes))
                    .thenRun(() -> isFilling.set(false));
            log.info("Наполнение кэша прошло успешно");
        }
    }
}
