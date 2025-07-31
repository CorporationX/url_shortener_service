package faang.school.urlshortenerservice.model;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import faang.school.urlshortenerservice.config.MainConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final MainConfig mainConfig;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean generationInProgress = new AtomicBoolean(false);

    @Value("${hashes.numberOfHashes}")
    private int numberOfHashes;
    private ArrayBlockingQueue<String> hashQueue;

    @PostConstruct
    public void init() {
        this.hashQueue = new ArrayBlockingQueue<>(numberOfHashes);
        hashQueue.addAll(hashRepository.getHashBatchOf(numberOfHashes));
    }

    public String getHash() {
        if (ifReachedThreshold()) {
            generateHashBatchAsync();
        }

        return hashQueue.poll();
    }

    private boolean ifReachedThreshold() {
        return hashQueue.size() * 100 < mainConfig.getNumberOfHashes() * mainConfig.getMimThresholdPercent();
    }

    @Async("customTaskExecutor")
    public void generateHashBatchAsync() {
        if(!generationInProgress.compareAndSet(false, true)) {
            log.warn("The thread is task is already running. Skipping");
            return;
        }

        try {
            int requestedHashesNumber = mainConfig.getNumberOfHashes() - hashQueue.size();
            List<Hash> batch = hashGenerator.generateBatch();
            hashQueue.addAll(
                batch.stream()
                    .map(Hash::getHash)
                    .limit(requestedHashesNumber)
                    .toList()
            );
        } finally {
            generationInProgress.set(false);
        }
    }
}
