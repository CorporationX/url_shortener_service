package faang.school.url_shortener_service.generator;

import faang.school.url_shortener_service.cache.HashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGeneratorScheduler {

    private final AsynchronousHashGenerator asynchronousHashGenerator;
    private final HashCache hashCache;

    @Value("${hash.batch}")
    private int hashBatchSize;

    @Scheduled(cron = "${hash.cache.refill-cron}")
    public void scheduledHashCacheRefill() {
        log.info("Scheduled hash refill triggered on thread: {}", Thread.currentThread().getName());
        asynchronousHashGenerator.getHashesAsynchronously(hashBatchSize)
               .thenAccept(hashCache::offerToCacheOrStoreRest);
    }
}