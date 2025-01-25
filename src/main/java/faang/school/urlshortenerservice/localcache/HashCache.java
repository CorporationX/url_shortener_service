package faang.school.urlshortenerservice.localcache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
@Data
@RequiredArgsConstructor
public class HashCache {

    private final ExecutorService executorService;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;


    @Value("${hashCache.capacity}")
    private int hashCacheCapacity;
    @Value("${hashCache.percentage}")
    private int minimumPercentage;

    private AtomicBoolean filling = new AtomicBoolean(false);
    ArrayBlockingQueue<Hash> hashCache = new ArrayBlockingQueue<>(hashCacheCapacity);


    @Transactional
    @Async("executorService")
    public Hash getHash() {
        if (hashCache.size() * 100 / hashCacheCapacity < minimumPercentage) {
            if (filling.compareAndSet(false, true)) {
                executorService.submit(() -> {
                    List<String> hashList = hashRepository.getHashBatch(hashCache.remainingCapacity());
                    hashList.stream().map(Hash::new).forEach(hashCache::offer);
                    filling.set(true);
                });
                executorService.submit(() -> hashGenerator.generateBatch());
            }
        }
        return hashCache.poll();
    }
}
