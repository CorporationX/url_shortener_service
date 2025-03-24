package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CacheRefillerAsync {
    private final CacheRefillerTransactional transactionalRefiller;

    @Async("hashGenerator")
    public void refillRedisFromGenerator(List<Long> capacity) {
        transactionalRefiller.refillRedisFromGenerator(capacity);
    }
}
