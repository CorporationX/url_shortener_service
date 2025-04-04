package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCacheService {

    private final HashService hashService;

    @Value("${cache.size}")
    private long maxCacheSize;

    @Value("${cache.threshold-percent}")
    private int thresholdPercent;

    private Queue<String> cache;

    private final AtomicBoolean loadingInProgress = new AtomicBoolean(false);

    @PostConstruct
    private void initCache(){
        cache = new ConcurrentLinkedDeque<>();
        cache.addAll(hashService.getHashes(maxCacheSize));
        log.info("Кэш инициализирован в кол-ве {} хэшей", cache.size());
    }

    public String getHash(){
        if(isBelowThreshold()) {
            if(loadingInProgress.compareAndSet(false,true)) {
                hashService.getHashesAsync(maxCacheSize)
                        .thenAccept(cache::addAll)
                        .thenRun(() -> loadingInProgress.set(false));
            }
        }
        return cache.poll();
    }

    private boolean isBelowThreshold() {
        return cache.size() < (maxCacheSize * thresholdPercent) / 100;
    }
}
