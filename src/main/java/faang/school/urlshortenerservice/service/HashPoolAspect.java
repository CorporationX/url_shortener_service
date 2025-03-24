package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.HashPoolStatus;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class HashPoolAspect {

    private final CacheRefillerTransactional cacheRefillerTransactional;
    private final FreeHashRepository freeHashRepository;
    private final CacheRefillerAsync cacheRefillerAsync;

    @Value("${shortener.hash-pool.max-capacity}")
    private long maxCapacity;

    @Value("${shortener.hash-pool.refill-threshold-percent}")
    private int refillThresholdPercent;


    @AfterReturning(
            pointcut = "@annotation(faang.school.urlshortenerservice.annotation.RefillsHashPool)",
            returning = "status"
    )
    public void triggerRefillIfNeeded(HashPoolStatus status) {
        Long remaining = status.remaining();
        long threshold = maxCapacity * refillThresholdPercent / 100;
        long freeHashInDb = freeHashRepository.count();

        if (remaining == null) {
            refill(maxCapacity, freeHashInDb);
            return;
        }

        if (remaining >= threshold) {
            return;
        }
        long toRefill = maxCapacity - remaining;
        refill(toRefill, freeHashInDb);

    }

    private void refill(long toRefill, long freeHashInDb){
        if (freeHashInDb >= toRefill){
            cacheRefillerTransactional.refillRedisFromDb(toRefill);
            return;
        }

        List<Long> range = freeHashRepository.generateBatch(toRefill);
        cacheRefillerAsync.refillRedisFromGenerator(range);
    }
}
