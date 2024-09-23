package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncExecutorForHashCash {

    private final HashService hashService;
    private final HashGenerator hashGenerator;

    private final AtomicBoolean exclAccessAllowed = new AtomicBoolean(false);

    @Async("executor")
    @Transactional
    public void exclusiveTransferHashBatch(int batchSize, ConcurrentLinkedQueue<String> queue) {
        if (exclAccessAllowed.compareAndSet(false, true)) {
            List<String> hashBatch = hashService.getHashBatch(batchSize);
            queue.addAll(hashBatch);
            log.info("{} hashes added to hashCache: {}", queue.size(), hashBatch);
            exclAccessAllowed.set(false);
        }
    }

    @Async("executor")
    public void asyncGenerateBatch() {
        hashGenerator.generateBatch();
    }
}
