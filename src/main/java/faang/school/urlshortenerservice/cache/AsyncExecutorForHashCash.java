package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncExecutorForHashCash {

    private final HashService hashService;
    private final HashGenerator hashGenerator;

    Lock lock = new ReentrantLock();

    @Async("executor")
    @Transactional
    public void exclusiveTransferHashBatch(int batchSize, ConcurrentLinkedQueue<String> queue) {
        if (lock.tryLock()) {
            try {
                List<String> hashBatch = hashService.getHashBatch(batchSize);
                queue.addAll(hashBatch);
                log.info("{} hashes added to hashCache: {}", queue.size(), hashBatch);
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
