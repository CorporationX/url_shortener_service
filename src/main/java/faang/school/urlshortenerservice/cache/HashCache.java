package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.executor.ExecutorService;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Component
@Slf4j
public class HashCache {
    private final int threshold;

    private final ArrayBlockingQueue<String> cache;
    private final ExecutorService executorService;

    public HashCache(ExecutorService executorService,
                     HashRepository hashRepository,
                     @Value("${hash.cache.load-factor}") double loadFactor,
                     @Value("${hash.cache.capacity}") int capacity) {
        this.executorService = executorService;
        cache = new ArrayBlockingQueue<>(capacity);
        threshold = (int) Math.ceil(loadFactor * capacity);
        List<String> hashes = hashRepository.getHashBatch();
        cache.addAll(hashes);
        log.info("Added {} initial hashes to cache", hashes.size());
    }

    public String getHash() {
        String hash = cache.poll();
        if (cache.size() <= threshold) {
            executorService.fillCache(cache);
        }
        return hash;
    }
}
