package faang.school.urlshortenerservice.executor;

import faang.school.urlshortenerservice.hash_generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutorService {
    private final static ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock writeLock = lock.writeLock();
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    @Async("taskExecutor")
    public void fillCache(ArrayBlockingQueue<String> cache) {
        hashGenerator.generateBatch();
        try {
            writeLock.lock();
            List<String> hashBatch = hashRepository.getHashBatch();
            cache.addAll(hashBatch);
            log.info("Added {} hashes to cache", hashBatch.size());
        } finally {
            writeLock.unlock();
        }
    }
}
