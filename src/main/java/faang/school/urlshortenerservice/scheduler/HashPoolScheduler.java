package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.RedisHashPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashPoolScheduler {

    private final RedisHashPoolService redisHashPoolService;

    @Scheduled(fixedRateString = "${app.scheduler.hash_pool.interval_ms}")
    public void scheduleReplenish() {
        try {
            redisHashPoolService.maybeReplenishPool();
        } catch (Exception e) {
            log.error("Redis hash pool refill failed", e);
        }
    }
}