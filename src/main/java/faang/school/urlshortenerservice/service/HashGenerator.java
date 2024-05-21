package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    private final ThreadPoolTaskExecutor taskExecutor;

    public HashGenerator(HashRepository hashRepository,
                         Base62Encoder base62Encoder,
                         @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.hashRepository = hashRepository;
        this.base62Encoder = base62Encoder;
        this.taskExecutor = taskExecutor;
    }

    @Value("${hash.batch}")
    private int batchSize;

    @PostConstruct
    public void init() {
        log.info("sending a task to execute asynchronously {}", Thread.currentThread());
        taskExecutor.execute(() -> generateBatchAsync());
    }

    @Transactional
    public List<Hash> generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers).stream()
                .map(Hash::new)
                .toList();

        return hashRepository.saveAll(hashes);
    }


    @Async("taskExecutor")
    public CompletableFuture<List<Hash>> generateBatchAsync() {
        log.info("Generating batch of hashes asynchronously {}", Thread.currentThread());
        return CompletableFuture.completedFuture(generateBatch());
    }

}
