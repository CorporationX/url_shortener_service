package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class AsyncExecutorForHashCash {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    Lock lock = new ReentrantLock();

    @Async("executor")
    public void exclusiveTransferHashBatch(int batchSize, ConcurrentLinkedQueue<String> queue) {
        if (lock.tryLock()) {
            try {
                List<String> hashBatch = hashRepository.getHashBatch(batchSize);
                queue.addAll(hashBatch);
            } finally {
                lock.unlock();
            }
        }
    }

    @Async("executor")
    public void asyncGenerateBatch() {
        hashGenerator.generateBatch();
    }
}
