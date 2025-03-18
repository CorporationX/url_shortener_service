package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final LinkedBlockingQueue<String> hashCache = new LinkedBlockingQueue<>();
    private final Lock lock = new ReentrantLock();

    @Value("${hash.hash-cache.min-size:1000}")
    private int minSize;

    public String getHash() {
        if (hashCache.size() < minSize && lock.tryLock()) {
            getHashes();
        }

        try {
            return hashCache.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("")
    public void getHashes() {
        try {
            List<String> hashes = hashRepository.getHashBatch();
            hashCache.addAll(hashes);
            hashGenerator.generateBatch();
        } finally {
            lock.unlock();
        }
    }
}
