package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashService hashService;
    private final HashGenerator hashGenerator;

    @Value("${spring.cache.capacity}")
    private int cacheCapacity;

    @Value("${spring.cache.min-size-percent}")
    private int minSizePercent;

    BlockingQueue<String> hashQueue;

    @PostConstruct
    private void init() {
        hashQueue = new ArrayBlockingQueue<>(cacheCapacity);
        hashGenerator.generateHash()
                .thenCompose(ignored -> hashService.findAndDelete())
                .thenAccept(hashQueue::addAll);
    }

    public Optional<String> get() {
        if (hasReachedAllowedPercentCapacity())
            fillCache();
        return Optional.ofNullable(hashQueue.poll());
    }

    private void fillCache() {
        hashService.findAndDelete()
                .thenAccept(hashQueue::addAll);
        hashGenerator.generateHash();
    }

    private boolean hasReachedAllowedPercentCapacity() {
        return hashQueue.size() <= cacheCapacity * minSizePercent / 100;
    }
}
