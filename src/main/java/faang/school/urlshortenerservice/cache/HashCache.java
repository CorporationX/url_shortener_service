package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    @Value("${hash.batch-size}")
    private int batchSize;
    @Value("${hash.cache.capacity}")
    private int capacity;
    @Value("${hash.cache.fill-percent}")
    private int fillPercent;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private BlockingQueue<Hash> hashes;

    @PostConstruct
    public void init(){
        hashes = new ArrayBlockingQueue<>(capacity);
        fillCache();
    }

    public Hash getHash() {
        if (hashes.size() / (capacity / 100.0) < fillPercent){
            if (isFilling.compareAndSet(false, true)){
                fillCache();
            }
        }
        return hashes.poll();
    }

    @Async("hashExecutor")
    public void fillCache() {
        int batch = batchSize - hashes.size();
        hashes.addAll(hashRepository.getHashBatch(batch));
        hashGenerator.generateBatch(batch);
        isFilling.set(false);
    }
}
