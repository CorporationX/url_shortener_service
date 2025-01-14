package faang.school.urlshortenerservice.cach;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.hash.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${batchSize}")
    private long batchSize;
    @Value("${hashCash.queueCapacity}")
    private int queueCapacity;
    @Value("${hashCash.percent}")
    private float percent;
    @Value("${hashCash.batchSizeForRedis}")
    private int batchSizeForRedis;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private BlockingQueue<Hash> hashes;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    @PostConstruct
    public void init(){
        hashes = new ArrayBlockingQueue<>(queueCapacity);
        hashGenerator.generateBatch();
        hashes.addAll(hashRepository.getHashBatch(batchSizeForRedis));
    }

    public Hash getHash(){
        if (hashes.size() <= hashes.size() * queueCapacity){
            CompletableFuture.runAsync(()->{
                closed.compareAndSet(false,true);
                hashGenerator.generateBatch();
                List<Hash> newHashes = hashRepository.getHashBatch(batchSize);
                hashes.addAll(newHashes);
            });
        } return hashes.poll();

    }

}
