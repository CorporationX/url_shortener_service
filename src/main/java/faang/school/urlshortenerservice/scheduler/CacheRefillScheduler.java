package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.local.cache.LocalCache;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheRefillScheduler {

    private final LocalCache localCache;

    @Scheduled(cron = "${hash.cron:0 0 0 * * *}")
    public void scheduleCacheRefill() {
        localCache.replenishHashQueueAsync();
    }
}
