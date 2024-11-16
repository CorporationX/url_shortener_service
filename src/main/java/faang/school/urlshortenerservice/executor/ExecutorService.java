package faang.school.urlshortenerservice.executor;

import faang.school.urlshortenerservice.hash_generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.sql.In;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutorService {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    @Value("${hash.repository.limit}")
    private long limit;

    @Async("taskExecutor")
    public CompletableFuture<List<String>> fillCache() {
        if (hashRepository.getHashCount() < limit) {
            hashGenerator.generateBatch();
        }
        List<String> hashBatch = hashRepository.getHashBatch();
        log.info("Added {} hashes to cache", hashBatch.size());
        return CompletableFuture.completedFuture(hashBatch);
    }
}
