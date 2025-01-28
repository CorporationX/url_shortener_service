package faang.school.urlshortenerservice.localcache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${hashCache.capacity:5000}")
    private int hashCacheCapacity;
    @Value("${hashCache.percentage:20}")
    private int minimumPercentage;
    @Value("${hash.range}")
    private int batch;

    private AtomicBoolean filling = new AtomicBoolean(false);
    private ArrayBlockingQueue<Hash> hashCache;

    @PostConstruct
    public void init() {
        hashCache = new ArrayBlockingQueue<>(hashCacheCapacity);
        hashCache.addAll(hashRepository.getHashBatch(batch).stream().map(Hash::new).toList());
    }


    @Transactional
    public Hash getHash() {
        if (hashCache.size() * 100 / hashCacheCapacity < minimumPercentage) {
            if (filling.compareAndSet(false, true)) {
                executorService.submit(() -> {
                    List<String> hashList = hashRepository.getHashBatch(hashCache.remainingCapacity());
                    hashList.stream().map(Hash::new).forEach(hashCache::offer);
                    hashGenerator.generateBatch();
                    filling.set(true);
                });

            }
        }
        return hashCache.poll();
    }
}
